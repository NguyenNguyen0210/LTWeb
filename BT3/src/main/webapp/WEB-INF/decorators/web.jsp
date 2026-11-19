<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <%@ include file="/common/head.jspf" %>
    <title><sitemesh:write property="title" /></title>
    <sitemesh:write property="head" />
</head>
<body class="layout-web">
<a class="visually-hidden-focusable d-inline-block p-2" href="#main">Đến nội dung chính</a>
<%@ include file="/common/web/header.jspf" %>
<main id="main" class="layout-main container py-4 py-md-5" tabindex="-1">
    <%@ include file="/common/flash.jspf" %>
    <sitemesh:write property="body" />
</main>
<%@ include file="/common/web/footer.jspf" %>
</body>
</html>
