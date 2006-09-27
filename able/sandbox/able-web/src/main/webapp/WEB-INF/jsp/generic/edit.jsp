<%@ include file="/WEB-INF/header.jsp" %>
<html>
<body>

<stripes:form action="${actionBean.actionUri}">
    <stripes:hidden name="entity"/>

    <stripes:errors/>

    <table class="display">

        <c:forEach items="${actionBean.entityInfo.editProperties}" var="property">
            <tr>
                <th>
                    <stripes:label for="entity.${property.name}">${property.displayName}</stripes:label>
                    :
                </th>
                <td>
                    <%-- TODO show different controls based on the property metadata --%>
                    <stripes:text name="entity.${property.name}"/>
                </td>
            </tr>
        </c:forEach>

    </table>

    <div class="buttons">
        <stripes:submit name="save" value="Save"/>
        <stripes:submit name="cancel" value="Cancel"/>
    </div>
</stripes:form>

</body>
</html>
