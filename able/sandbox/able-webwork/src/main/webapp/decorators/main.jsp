<%@ page import="com.opensymphony.able.service.UpgradeService"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib prefix="ww" uri="/webwork" %>
<decorator:useHtmlPage id="p"/>
<html>
<head>
    <title>Able</title>
    <ww:head/>
    <decorator:head/>
</head>

<body <decorator:getProperty property="body.onload" writeEntireProperty="true"/>>

<div id="container">

    <div id="widecolumn">
		<div id="page">
			<decorator:body/>
            <div class="copyright">
                Able-based App v1.0.<%= UpgradeService.getBuildNumber() %> | Copyright &copy; 2006, Acme Corp
            </div>
        </div>
	</div>

    <div id="sidecolumn">
        <div id="controlPanel" class="navigationBox">
            <h3>Menu</h3>
            <ul>
                <ww:if test="currentUser != null">
                    <li><a href="<ww:url action="user/settings" method="input"/>">My Settings</a></li>
                    <li><a href="<ww:url value="/user/${currentUser.id}/profile"/>">My Profile</a></li>
                    <li><a href="<ww:url action="logout"/>">Logout</a></li>
                </ww:if>
                <ww:else>
                    <li><a href="<ww:url action="register" method="input"/>">Register</a></li>
                    <li><a href="<ww:url action="login" method="input"/>">Login</a></li>
                </ww:else>
            </ul>
        </div>
    </div>
</div>

</body>
</html>