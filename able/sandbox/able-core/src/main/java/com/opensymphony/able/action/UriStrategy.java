/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.able.action;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @version $Revision$
 */
public class UriStrategy {

    public String getEntityPrimaryKeyString(JpaCrudActionSupport action) {
        HttpServletRequest request = action.getContext().getRequest();
        /*
        String requestURI = request.getRequestURI();
        
        // lets assume the last part of the URI is the primary key as a String value
        int idx = requestURI.lastIndexOf('/');
        if (idx >= 0) {
            return requestURI.substring(idx + 1);
        }
        */
        return request.getParameter("id");
    }
    
    public String getHomeUri(JpaCrudActionSupport action) {
        return "/" + action.getEntityUri() + "/index.jsp";
    }
    
    public String getViewUri(JpaCrudActionSupport action) {
        return "/" + action.getEntityUri() + "/view.jsp";
    }
    
    public String getEditUri(JpaCrudActionSupport action) {
        return "/" + action.getEntityUri() + "/edit.jsp?id=" + action.getId();
    }
    
}