package org.apache.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0(the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;

/**
 * @author <a href="mailto:jubu@volny.cz">Juraj Burian</a>
 * @version $Id:$
 */
public abstract class AbstractAPTMojo extends AbstractMojo
{
    protected static final String PATH_SEPARATOR = //
    System.getProperty( "path.separator" );

    protected static final String FILE_SEPARATOR = //
    System.getProperty( "file.separator" );

    /**
     * Integer returned by the Apt compiler to indicate success.
     */
    private static final int APT_COMPILER_SUCCESS = 0;

    /**
     * class in tools.jar that implements APT
     */
    private static final String APT_ENTRY_POINT = "com.sun.tools.apt.Main";

    /**
     * method used to compile.
     */
    private static final String APT_METHOD_NAME = "process";

    /**
     * store info about modification of system classpath for Apt compiler
     */
    private static boolean isClasspathModified;

    /**
     * Whether to include debugging information in the compiled class files. The
     * default value is true.
     * 
     * @parameter expression="${maven.compiler.debug}" default-value="true"
     * @readonly
     */
    private boolean debug;

    /**
     * Comma separated list of "-A" options: Next two examples are equivalent:
     * 
     * <pre>
     *         &lt;A&gt;-Adebug,-Aloglevel=3&lt;/A&gt;
     * </pre>
     * <pre>
     *         &lt;A&gt;debug, loglevel=3&lt;/A&gt;
     * </pre>
     * 
     * @parameter alias="A"
     */
    private String aptOptions;

    /**
     * Output source locations where deprecated APIs are used
     * 
     * @parameter
     */
    private boolean showDeprecation;

    /**
     * Output warnings
     * 
     * @parameter
     */
    private boolean showWarnings;

    /**
     * The -encoding argument for the Apt
     * 
     * @parameter
     */
    private String encoding;

    /**
     * run Apt in verbode mode
     * 
     * @parameter expression="${verbose}" default-value="false"
     */
    protected boolean verbose;

    /**
     * The -nocompile argument for the Apt
     * 
     * @parameter default-value="true"
     */
    private boolean nocompile;

    /**
     * The granularity in milliseconds of the last modification date for testing
     * whether a source needs recompilation
     * 
     * @parameter expression="${lastModGranularityMs}" default-value="0"
     */
    protected int staleMillis;

    /**
     * Name of AnnotationProcessorFactory to use; bypasses default discovery
     * process
     * 
     * @parameter
     */
    private String factory;

    /**
     * The directory to run the compiler from if fork is true.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    protected File builddir;

    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    protected abstract List getClasspathElements();

    protected abstract List getCompileSourceRoots();

    protected abstract File getOutputDirectory();

    protected abstract String getGenerated();

    protected abstract SourceInclusionScanner getSourceInclusionScanner();

    public void execute() throws MojoExecutionException
    {
        getLog().debug( "Using apt compiler" );
        List cmd = new LinkedList();

        int result = APT_COMPILER_SUCCESS;
        StringWriter writer = new StringWriter();
        // finally invoke APT
        // Use reflection to be able to build on all JDKs:
        try
        {
            // we need to have tools.jar in lasspath
            // due to bug in Apt compiler, system classpath must be modified but in future:
            // TODO try separate ClassLoader (see Plexus compiler api)
            if( false == isClasspathModified )
            {
                URL toolsJar = new File( System.getProperty( "java.home" ),
                        "../lib/tools.jar" ).toURL();
                Method m = URLClassLoader.class.getDeclaredMethod( "addURL",
                        new Class[] { URL.class } );
                m.setAccessible( true );
                m.invoke( this.getClass().getClassLoader()
                        .getSystemClassLoader(), new Object[] { toolsJar } );
                isClasspathModified = true;
            }
            // init comand line
            setAptCommandlineSwitches( cmd );
            setAptSpecifics( cmd );
            setStandards( cmd );
            setClasspath( cmd );
            if( false == setSourcepath( cmd ) )
            {
                if( true == getLog().isDebugEnabled() )
                {
                    getLog().debug( "there are not stale sources." );
                }
                return;
            }
            Class c = this.getClass().forName( APT_ENTRY_POINT ); // getAptCompilerClass();
            Object compiler = c.newInstance();
            Method compile = c.getMethod( APT_METHOD_NAME, new Class[] {
                    PrintWriter.class, (new String[] {}).getClass() } );
            result = ((Integer) //
            compile.invoke( compiler, new Object[] { new PrintWriter( writer ),
                    cmd.toArray( new String[cmd.size()] ) } )).intValue();

        } catch ( Exception ex )
        {
            throw new MojoExecutionException( "Error starting apt compiler", ex );
        } finally
        {
            if( result != APT_COMPILER_SUCCESS )
            {
                throw new MojoExecutionException( this, "Compilation error.",
                        writer.getBuffer().toString() );
            }
            if( true == getLog().isDebugEnabled() )
            {
                String r = writer.getBuffer().toString();
                if( 0 != r.length() )
                {
                    getLog().debug( r );
                }
                getLog().debug( "Apt finished." );
            }
        }
    }

    private void setAptCommandlineSwitches( List cmd )
    {
        if( null == aptOptions )
        {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer( aptOptions.trim(), "," );
        while( tokenizer.hasMoreElements() )
        {
            String option = tokenizer.nextToken().trim();
            if( false == option.startsWith( "-A" ) )
            {
                option = "-A" + option;
            }
            cmdAdd( cmd, option );
        }
    }

    private void setAptSpecifics( List cmd ) throws MojoExecutionException
    {
        try
        {
            String g = builddir.getAbsolutePath() + FILE_SEPARATOR
                    + getGenerated();
            File generatedDir = new File( g );
            cmdAdd( cmd, "-s", generatedDir.getCanonicalPath() );
            if( false == generatedDir.exists() )
            {
                generatedDir.mkdirs();
            }
        } catch ( Exception e )
        {
            throw new MojoExecutionException( //
                    "Generated directory is invalid.", e );
        }
        if( true == nocompile )
        {
            cmdAdd( cmd, "-nocompile" );
        }
        if( null != factory && 0 != factory.length() )
        {
            cmdAdd( cmd, "-factory", factory );
        }
    }

    private void setStandards( List cmd ) throws MojoExecutionException
    {
        if( true == debug )
        {
            cmdAdd( cmd, "-g" );
        }
        if( false == showWarnings )
        {
            cmdAdd( cmd, "-nowarn" );
        }
        if( true == showDeprecation )
        {
            cmdAdd( cmd, "-depecation" );
        }
        if( null != encoding )
        {
            cmdAdd( cmd, "-encoding", encoding );
        }
        if( true == verbose )
        {
            cmdAdd( cmd, "-verbose" );
        }
        // add output directory
        try
        {
            if( false == getOutputDirectory().exists() )
            {
                getOutputDirectory().mkdirs();
            }
            cmdAdd( cmd, "-d", getOutputDirectory().getCanonicalPath() );
        } catch ( Exception ex )
        {
            throw new MojoExecutionException( //
                    "Output directory is invalid.", ex );
        }
    }

    private boolean setSourcepath( List cmd ) throws MojoExecutionException
    {
        boolean has = false;
        // sources ....
        Iterator it = getCompileSourceRoots().iterator();
        while( true == it.hasNext() )
        {
            File srcFile = new File( (String) it.next() );
            if( true == srcFile.isDirectory() )
            {
                Collection sources = null;
                try
                {
                    sources = //
                    getSourceInclusionScanner().getIncludedSources( srcFile,
                            getOutputDirectory() );
                } catch ( Exception ex )
                {
                    throw new MojoExecutionException(
                            "Can't agregate sources.", ex );
                }
                if( getLog().isDebugEnabled() )
                {
                    getLog().debug(
                            "sources from: " + srcFile.getAbsolutePath() );
                    String s = "";
                    for( Iterator jt = sources.iterator(); true == jt.hasNext(); )
                    {
                        s += jt.next() + "\n";
                    }
                    getLog().debug( s );
                }
                Iterator jt = sources.iterator();
                while( true == jt.hasNext() )
                {
                    File src = (File) jt.next();
                    cmd.add( src.getAbsolutePath() );
                    has = true;
                }
            }
        }
        return has;
    }

    private void setClasspath( List cmd ) throws MojoExecutionException
    {
        StringBuffer buffer = new StringBuffer();
        for( Iterator it = getClasspathElements().iterator(); true == it
                .hasNext(); )
        {
            buffer.append( it.next() );
            if( it.hasNext() )
            {
                buffer.append( PATH_SEPARATOR );
            }
        }
        cmdAdd( cmd, "-classpath", buffer.toString() );
    }

    private void cmdAdd( List cmd, String arg )
    {
        /**
         * OBSOLETE
         * if( true == getLog().isDebugEnabled() ) { getLog().debug(
         * arg ); }
         */
        cmd.add( arg );
    }

    private void cmdAdd( List cmd, String arg1, String arg2 )
    {
        /**
         * OBSOLETE
         * if( true == getLog().isDebugEnabled() ) { getLog().debug(
         * arg1 + " " + arg2 ); }
         */
        cmd.add( arg1 );
        cmd.add( arg2 );
    }
}
