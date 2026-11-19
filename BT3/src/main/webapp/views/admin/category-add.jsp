<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head><title>Thêm Category - LTWeb</title></head>
<body>
<div class="card">
        <div class="card-head">
            <div>
                <h1>Thêm Category</h1>
                <p>Điền tên, ảnh và trạng thái rồi bấm Insert</p>
            </div>
            <a class="btn btn-light" href="${ctx}/admin/categories">‹ Quay lại</a>
        </div>

        <div class="card-body">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${fn:escapeXml(error)}</div>
            </c:if>

            <%-- enctype multipart/form-data bat buoc phai co de upload file --%>
            <form action="${ctx}/admin/category/insert" method="post" enctype="multipart/form-data">

                <div class="field">
                    <label for="categoryname">Category name</label>
                    <input type="text" id="categoryname" name="categoryname" maxlength="50"
                           value="${fn:escapeXml(categoryname)}" placeholder="Ví dụ: Điện thoại" required>
                    <div class="hint">Tối đa 50 ký tự, không được để trống.</div>
                </div>

                <div class="field">
                    <label for="images">Link images</label>
                    <input type="text" id="images" name="images"
                           value="${fn:escapeXml(images)}" placeholder="https://... (có thể bỏ trống)">
                    <div class="hint">Dán link ảnh online, hoặc upload file ở ô bên dưới.</div>
                </div>

                <div class="field">
                    <label for="images1">Upload images</label>
                    <input type="file" id="images1" name="images1" accept="image/*">
                    <div class="hint">Chấp nhận jpg, jpeg, png, gif, webp, bmp · tối đa 5MB.
                        Nếu có cả link và file thì file upload được ưu tiên.</div>
                </div>

                <div class="field">
                    <label>Status</label>
                    <div class="radio-row">
                        <label>
                            <input type="radio" name="status" value="1"
                                   <c:if test="${empty status or status == 1}">checked</c:if>>
                            Hoạt động
                        </label>
                        <label>
                            <input type="radio" name="status" value="0"
                                   <c:if test="${status == 0}">checked</c:if>>
                            Khóa
                        </label>
                    </div>
                </div>

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Insert</button>
                    <a class="btn btn-light" href="${ctx}/admin/categories">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
