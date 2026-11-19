<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head><title>Thêm video</title></head>
<body>

<div class="card">
    <div class="card-head">
        <div>
            <h1>Thêm video</h1>
            <p>Chọn file video để upload, hoặc dán link YouTube / mp4</p>
        </div>
        <a class="btn btn-light" href="${ctx}/video">‹ Quay lại</a>
    </div>

    <div class="card-body">
        <c:if test="${not empty error}">
            <div class="alert alert-error">${fn:escapeXml(error)}</div>
        </c:if>

        <form action="${ctx}/video/insert" method="post" enctype="multipart/form-data">

            <div class="field">
                <label for="title">Tiêu đề video</label>
                <input type="text" id="title" name="title" maxlength="200"
                       value="${fn:escapeXml(video.title)}" placeholder="Ví dụ: Review iPhone 15" required>
            </div>

            <div class="field">
                <label for="description">Mô tả</label>
                <textarea id="description" name="description" rows="3" maxlength="500"
                          placeholder="Mô tả ngắn về video">${fn:escapeXml(video.description)}</textarea>
            </div>

            <div class="field">
                <label for="videoUrl">Link video (YouTube hoặc .mp4)</label>
                <input type="text" id="videoUrl" name="videoUrl"
                       value="${fn:escapeXml(video.videoUrl)}"
                       placeholder="https://www.youtube.com/watch?v=... hoặc https://.../video.mp4">
            </div>

            <div class="field">
                <label for="videoFile">Hoặc upload file video</label>
                <input type="file" id="videoFile" name="videoFile" accept="video/*">
                <div class="hint">mp4, webm, ogg, mov · tối đa 100MB. Nếu có cả link và file thì file được ưu tiên.</div>
            </div>

            <div class="field">
                <label for="poster">Link ảnh poster</label>
                <input type="text" id="poster" name="poster" value="${fn:escapeXml(video.poster)}"
                       placeholder="https://... (có thể bỏ trống)">
            </div>

            <div class="field">
                <label for="posterFile">Hoặc upload ảnh poster</label>
                <input type="file" id="posterFile" name="posterFile" accept="image/*">
                <div class="hint">jpg, png, gif, webp · tối đa 5MB.</div>
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
                               <c:if test="${empty video or video.active == 1}">checked</c:if>>
                        Công khai
                    </label>
                    <label>
                        <input type="radio" name="active" value="0"
                               <c:if test="${video.active == 0}">checked</c:if>>
                        Ẩn
                    </label>
                </div>
            </div>

            <div class="actions">
                <button class="btn btn-primary" type="submit">Thêm video</button>
                <a class="btn btn-light" href="${ctx}/video">Hủy</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>
