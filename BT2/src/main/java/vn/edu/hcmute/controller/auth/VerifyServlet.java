package vn.edu.hcmute.controller.auth;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmute.entity.User;
import vn.edu.hcmute.service.IUserService;
import vn.edu.hcmute.service.MailService;
import vn.edu.hcmute.service.UserServiceImpl;
import vn.edu.hcmute.util.Flash;

/**
 * Trang nhap OTP de kich hoat tai khoan.
 * GET  /verify?email=...            -> hien form
 * POST /verify                      -> kiem tra OTP
 * POST /verify (action=resend)      -> gui lai OTP
 */
@WebServlet("/verify")
public class VerifyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || email.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }
        forward(req, resp, email, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String action = req.getParameter("action");

        if (email == null || email.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        if ("resend".equals(action)) {
            try {
                boolean sent = userService.resendActivationOtp(email);
                Flash.success(req, sent
                        ? "Đã gửi lại mã OTP tới " + email
                        : "Đã tạo mã mới. Chưa cấu hình SMTP nên mã được in ra console Tomcat.");
            } catch (RuntimeException e) {
                Flash.error(req, e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + "/verify?email=" + java.net.URLEncoder.encode(email,
                    java.nio.charset.StandardCharsets.UTF_8));
            return;
        }

        String otp = req.getParameter("otp");
        try {
            userService.activate(email, otp);
            Flash.success(req, "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (RuntimeException e) {
            forward(req, resp, email, e.getMessage());
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String email, String error)
            throws ServletException, IOException {
        User user = userService.findByEmail(email);
        req.setAttribute("email", email);
        req.setAttribute("fullname", user == null ? null : user.getFullname());
        req.setAttribute("mailConfigured", MailService.isConfigured());
        if (error != null) {
            req.setAttribute("error", error);
        }
        req.getRequestDispatcher("/views/auth/verify.jsp").forward(req, resp);
    }
}
