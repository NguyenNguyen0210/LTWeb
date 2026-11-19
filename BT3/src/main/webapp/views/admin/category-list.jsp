<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head><title>Danh sách Category - LTWeb</title></head>
<body>
<div class="card">

        <div class="card-head">
            <div>
                <h1>Danh sách Category</h1>
                <p>
                    Tổng <strong>${total}</strong> danh mục
                    <c:if test="${empty keyword and totalPages > 0}">
                        · Trang ${currentPage}/${totalPages}
                    </c:if>
                    <c:if test="${not empty keyword}">
                        · Kết quả cho từ khóa "<strong>${fn:escapeXml(keyword)}</strong>"
                    </c:if>
                </p>
            </div>

            <div style="display:flex;gap:10px;align-items:center;flex-wrap:wrap">
                <form class="search" method="get" action="${ctx}/admin/categories">
                    <input type="text" name="keyword" value="${fn:escapeXml(keyword)}" placeholder="Tìm theo tên...">
                    <button class="btn btn-light" type="submit">Tìm</button>
                    <c:if test="${not empty keyword}">
                        <a class="btn btn-light" href="${ctx}/admin/categories">Bỏ lọc</a>
                    </c:if>
                </form>
                <a class="btn btn-primary" href="${ctx}/admin/category/add">+ Thêm danh mục</a>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty listcate}">
                <div class="empty">Chưa có danh mục nào. Bấm <strong>+ Thêm danh mục</strong> để bắt đầu.</div>
            </c:when>
            <c:otherwise>
                <table class="grid">
                    <thead>
                    <tr>
                        <th style="width:56px">STT</th>
                        <th style="width:104px">Images</th>
                        <th>Category name</th>
                        <th style="width:130px">Status</th>
                        <th style="width:150px">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${listcate}" var="cate" varStatus="STT">
                        <tr>
                            <td class="muted">${STT.index + 1}</td>

                            <td>
                                <c:choose>
                                    <c:when test="${empty cate.images}">
                                        <c:url value="/image?fname=avatar.png" var="imgUrl" />
                                        <img class="thumb" src="${fn:escapeXml(imgUrl)}" alt="no image">
                                    </c:when>
                                    <c:when test="${fn:startsWith(cate.images, 'http')}">
                                        <c:url value="${cate.images}" var="imgUrl" />
                                        <img class="thumb" src="${fn:escapeXml(imgUrl)}" alt="${fn:escapeXml(cate.categoryname)}">
                                    </c:when>
                                    <c:otherwise>
                                        <c:url value="/image?fname=${cate.images}" var="imgUrl" />
                                        <img class="thumb" src="${fn:escapeXml(imgUrl)}" alt="${fn:escapeXml(cate.categoryname)}">
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <strong>${fn:escapeXml(cate.categoryname)}</strong>
                                <div class="muted">#${cate.categoryid}</div>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${cate.status == 1}">
                                        <span class="badge badge-on">Hoạt động</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-off">Khóa</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <a class="btn btn-light btn-sm"
                                   href="${ctx}/admin/category/edit?id=${cate.categoryid}">Sửa</a>
                                <a class="btn btn-danger btn-sm"
                                   href="${ctx}/admin/category/delete?id=${cate.categoryid}"
                                   onclick="return confirm('Xóa danh mục này?');">Xóa</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <%-- Phan trang: chi hien khi dang xem toan bo danh sach --%>
        <c:if test="${empty keyword and totalPages > 1}">
            <div class="pager">
                <c:if test="${currentPage > 1}">
                    <a href="${ctx}/admin/categories?page=${currentPage - 1}">‹ Trước</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${i == currentPage}">
                            <span class="cur">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${ctx}/admin/categories?page=${i}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="${ctx}/admin/categories?page=${currentPage + 1}">Sau ›</a>
                </c:if>

                <span class="info">Trang ${currentPage} / ${totalPages}</span>
            </div>
        </c:if>

    </div>
</body>
</html>
