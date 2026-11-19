<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head><title>Sửa Category - LTWeb</title></head>
<body>
<div class="card">
        <div class="card-head">
            <div>
                <h1>Sửa Category #${cate.categoryid}</h1>
                <p>Cập nhật thông tin rồi bấm Update</p>
            </div>
            <a class="btn btn-light" href="${ctx}/admin/categories">‹ Quay lại</a>
        </div>

        <div class="card-body">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${fn:escapeXml(error)}</div>
            </c:if>

            <form action="${ctx}/admin/category/update" method="post" enctype="multipart/form-data">
                <input type="hidden" name="categoryid" value="${cate.categoryid}">

                <div class="field">
                    <label for="categoryname">Category name</label>
                    <input type="text" id="categoryname" name="categoryname" maxlength="50"
                           value="${fn:escapeXml(cate.categoryname)}" required>
                </div>

                <div class="field">
                    <label for="images">Link images</label>
                    <input type="text" id="images" name="images" value="${fn:escapeXml(cate.images)}">
                    <div class="hint">Sửa link trực tiếp, hoặc upload file mới ở ô bên dưới.</div>
                </div>

                <div class="field">
                    <label>Ảnh hiện tại</label>
                    <c:choose>
                        <c:when test="${empty cate.images}">
                            <c:url value="/image?fname=avatar.png" var="imgUrl" />
                            <img class="thumb-lg" src="${imgUrl}" alt="no image">
                        </c:when>
                        <c:when test="${fn:startsWith(cate.images, 'http')}">
                            <c:url value="${cate.images}" var="imgUrl" />
                            <img class="thumb-lg" src="${imgUrl}" alt="${fn:escapeXml(cate.categoryname)}">
                        </c:when>
                        <c:otherwise>
                            <c:url value="/image?fname=${cate.images}" var="imgUrl" />
                            <img class="thumb-lg" src="${imgUrl}" alt="${fn:escapeXml(cate.categoryname)}">
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="field">
                    <label for="images1">Upload images</label>
                    <input type="file" id="images1" name="images1" accept="image/*">
                    <div class="hint">Chọn file mới thì ảnh cũ (nếu là file upload) sẽ bị xóa khỏi ổ đĩa.</div>
                </div>

                <div class="field">
                    <label>Status</label>
                    <div class="radio-row">
                        <label>
                            <input type="radio" name="status" value="1"
                                   <c:if test="${cate.status == 1}">checked</c:if>>
                            Hoạt động
                        </label>
                        <label>
                            <input type="radio" name="status" value="0"
                                   <c:if test="${cate.status != 1}">checked</c:if>>
                            Khóa
                        </label>
                    </div>
                </div>

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Update</button>
                    <a class="btn btn-light" href="${ctx}/admin/categories">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
