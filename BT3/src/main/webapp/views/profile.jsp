<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head><title>Hồ sơ · LTWeb</title></head>
<body>
<div class="mx-auto profile-page">
    <h1 class="h3 mb-4">Hồ sơ của tôi</h1>
    <c:if test="${not empty error}">
        <div class="alert alert-error" role="alert"><c:out value="${error}" /></div>
    </c:if>
    <div class="row g-4">
        <div class="col-lg-6">
            <section class="card h-100" aria-labelledby="details-title">
                <div class="card-body">
                    <h2 id="details-title" class="h5 mb-3">Thông tin cá nhân</h2>
                    <form action="${ctx}/profile" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="details">
                        <input type="hidden" name="csrf" value="${csrf}">

                        <div class="d-flex flex-wrap align-items-center gap-3 mb-3">
                            <c:choose>
                                <c:when test="${empty profile.avatar}">
                                    <span class="avatar-sm profile-avatar" aria-hidden="true">
                                        <c:out value="${fn:toUpperCase(fn:substring(profile.displayName, 0, 1))}" />
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${fn:startsWith(profile.avatar, 'https://') or fn:startsWith(profile.avatar, 'http://')}">
                                            <c:set var="avatarUrl" value="${profile.avatar}" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:url value="/image" var="avatarUrl">
                                                <c:param name="fname" value="${profile.avatar}" />
                                            </c:url>
                                        </c:otherwise>
                                    </c:choose>
                                    <img class="profile-avatar" src="${fn:escapeXml(avatarUrl)}" alt="Ảnh đại diện hiện tại">
                                </c:otherwise>
                            </c:choose>
                            <div class="field flex-grow-1 mb-0">
                                <label for="avatar">Ảnh đại diện</label>
                                <input id="avatar" name="avatar" type="file" accept="image/jpeg,image/png,image/gif"
                                       aria-describedby="avatar-hint">
                                <div id="avatar-hint" class="form-text">JPEG, PNG hoặc GIF. Tối đa 5 MB.</div>
                            </div>
                        </div>

                        <div class="field">
                            <label for="fullname">Họ tên</label>
                            <input id="fullname" name="fullname" type="text" required maxlength="100" autocomplete="name"
                                   value="${fn:escapeXml(formFullname != null ? formFullname : profile.fullname)}">
                        </div>
                        <div class="field">
                            <label for="phone">Số điện thoại</label>
                            <input id="phone" name="phone" type="tel" maxlength="20" autocomplete="tel"
                                   value="${fn:escapeXml(formPhone != null ? formPhone : profile.phone)}">
                        </div>
                        <button class="btn btn-primary" type="submit">Lưu hồ sơ</button>
                    </form>
                </div>
            </section>
        </div>

        <div class="col-lg-6">
            <section class="card h-100" aria-labelledby="email-title">
                <div class="card-body">
                    <h2 id="email-title" class="h5 mb-2">Email đăng nhập</h2>
                    <p class="mb-3">Email hiện tại <strong><c:out value="${profile.email}" /></strong></p>

                    <c:if test="${not empty pending}">
                        <form action="${ctx}/profile" method="post" class="border-bottom pb-3 mb-3">
                            <input type="hidden" name="action" value="email-confirm">
                            <input type="hidden" name="csrf" value="${csrf}">
                            <div class="field">
                                <label for="email-otp">Mã xác nhận gửi tới <c:out value="${pending.newEmail}" /></label>
                                <input id="email-otp" name="otp" type="text" inputmode="numeric" maxlength="6"
                                       pattern="[0-9]{6}" autocomplete="one-time-code" required aria-describedby="otp-hint">
                                <div id="otp-hint" class="form-text">Mã có hiệu lực trong 5 phút.</div>
                            </div>
                            <button class="btn btn-primary" type="submit">Xác nhận email mới</button>
                        </form>
                    </c:if>

                    <form action="${ctx}/profile" method="post">
                        <input type="hidden" name="action" value="email-request">
                        <input type="hidden" name="csrf" value="${csrf}">
                        <div class="field">
                            <label for="newEmail">Email mới</label>
                            <input id="newEmail" name="newEmail" type="email" maxlength="100" autocomplete="email"
                                   value="${fn:escapeXml(formEmail != null ? formEmail : pending.newEmail)}" required>
                        </div>
                        <div class="field">
                            <label for="currentPassword">Mật khẩu hiện tại</label>
                            <input id="currentPassword" name="currentPassword" type="password" maxlength="72"
                                   autocomplete="current-password" required>
                        </div>
                        <button class="btn btn-light" type="submit">${empty pending ? 'Gửi mã xác nhận' : 'Gửi mã mới'}</button>
                    </form>
                </div>
            </section>
        </div>
    </div>
</div>
</body>
</html>
