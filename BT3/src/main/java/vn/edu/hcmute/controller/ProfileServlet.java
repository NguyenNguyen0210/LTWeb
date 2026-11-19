package vn.edu.hcmute.controller;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmute.entity.User;
import vn.edu.hcmute.service.EmailChange;
import vn.edu.hcmute.service.MailService;
import vn.edu.hcmute.service.ProfileService;
import vn.edu.hcmute.util.Flash;
import vn.edu.hcmute.util.UploadUtil;
import vn.edu.hcmute.util.View;

@WebServlet("/profile")
@MultipartConfig(maxFileSize = UploadUtil.IMAGE_MAX_BYTES, maxRequestSize = 6 * 1024 * 1024)
public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VIEW = "/views/profile.jsp";
    private static final String STATE = ProfileServlet.class.getName() + ".state";

    private final ProfileService service = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = activeUser(req, resp, session);
        if (user != null) {
            ProfileState state = state(session);
            synchronized (state) {
                render(req, resp, user, state);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = activeUser(req, resp, session);
        if (user == null) {
            return;
        }

        ProfileState state = state(session);
        synchronized (state) {
            try {
                // Tomcat can suppress parameters when multipart parsing fails. Inspect the parts first.
                if (req.getContentType() != null && req.getContentType().startsWith("multipart/form-data")) {
                    req.getParts();
                }
            } catch (IllegalStateException | IOException | ServletException e) {
                req.setAttribute("error", "Không đọc được tệp. Ảnh đại diện có giới hạn 5 MB.");
                render(req, resp, user, state);
                return;
            }

            String token = req.getParameter("csrf");
            if (token == null || token.length() != state.token.length()
                    || !MessageDigest.isEqual(state.token.getBytes(StandardCharsets.UTF_8),
                            token.getBytes(StandardCharsets.UTF_8))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String action = req.getParameter("action");
            if (action == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            try {
                switch (action) {
                    case "details" -> updateDetails(req, user);
                    case "email-request" -> requestCode(req, user, state);
                    case "email-confirm" -> confirmCode(req, user, state);
                    default -> throw new IllegalArgumentException("Thao tác không hợp lệ");
                }
            } catch (MailService.MailException e) {
                log("Không gửi được OTP đổi email", e);
                req.setAttribute("error", "Không gửi được mã xác nhận. Vui lòng thử lại.");
                render(req, resp, user, state);
                return;
            } catch (IllegalArgumentException | IllegalStateException e) {
                req.setAttribute("error", e.getMessage());
                render(req, resp, user, state);
                return;
            } catch (IOException e) {
                log("Không tải được ảnh đại diện", e);
                req.setAttribute("error", "Không tải được ảnh đại diện lên Cloudinary. Vui lòng thử lại.");
                render(req, resp, user, state);
                return;
            } catch (RuntimeException e) {
                log("Không lưu được yêu cầu cập nhật hồ sơ", e);
                req.setAttribute("error", "Không thể lưu thay đổi. Vui lòng thử lại.");
                render(req, resp, user, state);
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/profile");
        }
    }

    private void updateDetails(HttpServletRequest req, User user) throws IOException, ServletException {
        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");
        req.setAttribute("formFullname", fullname);
        req.setAttribute("formPhone", phone);
        service.updateDetails(user, fullname, phone, req.getContentType() != null
                && req.getContentType().startsWith("multipart/form-data") ? req.getPart("avatar") : null);
        Flash.success(req, "Đã lưu thông tin hồ sơ.");
    }

    private void requestCode(HttpServletRequest req, User user, ProfileState state) {
        String newEmail = req.getParameter("newEmail");
        req.setAttribute("formEmail", newEmail);
        if (Instant.now().isBefore(state.nextEmailRequest)) {
            throw new IllegalStateException("Vui lòng đợi 60 giây giữa các lần gửi mã.");
        }
        state.pending = service.requestEmailChange(user, newEmail, req.getParameter("currentPassword"));
        state.nextEmailRequest = Instant.now().plusSeconds(60);
        Flash.success(req, state.pending.isDelivered()
                ? "Đã gửi mã xác nhận đến email mới."
                : "Chế độ phát triển. Mã xác nhận được ghi vào console Tomcat.");
    }

    private void confirmCode(HttpServletRequest req, User user, ProfileState state) {
        service.confirmEmailChange(user, state.pending, req.getParameter("otp"));
        state.pending = null;
        Flash.success(req, "Đã xác nhận và cập nhật email.");
    }

    private User activeUser(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        User current = (User) session.getAttribute("user");
        User fresh = current == null ? null : service.findActiveUser(current.getUserid());
        if (fresh == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            session.setAttribute("user", fresh);
        }
        return fresh;
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, User user, ProfileState state)
            throws ServletException, IOException {
        resp.setHeader("Cache-Control", "no-store");
        req.setAttribute("profile", user);
        req.setAttribute("csrf", state.token);
        if (state.pending != null && state.pending.isUsable(Instant.now())) {
            req.setAttribute("pending", state.pending);
        }
        View.render(req, resp, VIEW);
    }

    private ProfileState state(HttpSession session) {
        synchronized (session) {
            ProfileState state = (ProfileState) session.getAttribute(STATE);
            if (state == null) {
                state = new ProfileState();
                session.setAttribute(STATE, state);
            }
            return state;
        }
    }

    private static final class ProfileState implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String token = UUID.randomUUID().toString();
        private Instant nextEmailRequest = Instant.EPOCH;
        private EmailChange pending;
    }
}
