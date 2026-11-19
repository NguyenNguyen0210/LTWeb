<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head><title>Sửa video</title></head>
<body>

<div class="card">
    <div class="card-head">
        <div>
            <h1>Sửa video</h1>
            <p>Mã video: <code>${fn:escapeXml(video.videoId)}</code></p>
        </div>
        <a class="btn btn-light" href="${ctx}/video">‹ Quay lại</a>
    </div>

    <div class="card-body">
        <c:if test="${not empty error}">
            <div class="alert alert-error">${fn:escapeXml(error)}</div>
        </c:if>

        <form action="${ctx}/video/update" method="post" enctype="multipart/form-data">
            <input type="hidden" name="videoId" value="${fn:escapeXml(video.videoId)}">

            <div class="field">
                <label for="title">Tiêu đề video</label>
                <input type="text" id="title" name="title" maxlength="200"
                       value="${fn:escapeXml(video.title)}" required>
            </div>

            <div class="field">
                <label for="description">Mô tả</label>
                <textarea id="description" name="description" rows="3" maxlength="500">${fn:escapeXml(video.description)}</textarea>
            </div>

            <div class="field">
                <label for="videoUrl">Link video (YouTube hoặc .mp4)</label>
                <input type="text" id="videoUrl" name="videoUrl" value="${fn:escapeXml(video.videoUrl)}">
                <div class="hint">
                    <c:choose>
                        <c:when test="${video.uploaded}">Đang dùng file upload: <code>${fn:escapeXml(video.videoUrl)}</code></c:when>
                        <c:otherwise>Đang dùng link bên trên.</c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="field">
                <label for="videoFile">Upload file video mới</label>
                <input type="file" id="videoFile" name="videoFile" accept="video/*">
                <div class="hint">Chọn file mới thì file cũ (nếu có) sẽ bị xóa khỏi ổ đĩa.</div>
            </div>

            <div class="field">
                <label for="poster">Link ảnh poster</label>
                <input type="text" id="poster" name="poster" value="${fn:escapeXml(video.poster)}">
            </div>

            <div class="field">
                <label>Poster hiện tại</label>
                <c:choose>
                    <c:when test="${empty video.poster}">
                        <div class="muted">Chưa có poster</div>
                    </c:when>
                    <c:when test="${video.posterUrl}">
                        <img class="thumb-lg" src="${video.poster}" alt="poster">
                    </c:when>
                    <c:otherwise>
                        <img class="thumb-lg" src="${ctx}/media?fname=${video.poster}" alt="poster">
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="field">
                <label for="posterFile">Upload ảnh poster mới</label>
                <input type="file" id="posterFile" name="posterFile" accept="image/*">
            </div>

            <div class="field">
                <label for="categoryId">Danh mục</label>
                <select id="categoryId" name="categoryId">
                    <option value="">-- Không chọn --</option>
                    <c:forEach items="${categories}" var="cate">
                        <option value="${cate.categoryid}"
                                <c:if test="${video.category.categoryid == cate.categoryid}">selected</c:if>>
                            ${fn:escapeXml(cate.categoryname)}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="field">
                <label>Trạng thái</label>
                <div class="radio-row">
                    <label>
                        <input type="radio" name="active" value="1"
                               <c:if test="${video.active == 1}">checked</c:if>>
                        Công khai
                    </label>
                    <label>
                        <input type="radio" name="active" value="0"
                               <c:if test="${video.active != 1}">checked</c:if>>
                        Ẩn
                    </label>
                </div>
            </div>

            <div class="actions">
                <button class="btn btn-primary" type="submit">Cập nhật</button>
                <a class="btn btn-light" href="${ctx}/video">Hủy</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>
