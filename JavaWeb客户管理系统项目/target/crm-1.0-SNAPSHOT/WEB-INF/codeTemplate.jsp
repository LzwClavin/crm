<%@ page import="com.lzw.crm.utils.UUIDUtil" %>
<%@ page import="com.lzw.crm.utils.DateTimeUtil" %>
<%@ page import="com.lzw.crm.settings.domain.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + "/";
    String id = UUIDUtil.getUUID();
    String createBy = DateTimeUtil.getSysTime();
    String createTime = ((User) request.getSession().getAttribute("user")).getName();
%>
<html>
<head>
    <base href="<%=path%>">
    <title></title>
</head>
<body>
<script>
    $.ajax({
        type: "",
        url: "",
        data: {},
        dataType: "json",
        success: function (data) {

        }
    })
</script>
</body>
</html>