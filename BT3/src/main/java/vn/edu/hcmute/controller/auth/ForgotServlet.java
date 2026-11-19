package vn.edu.hcmute.controller.auth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmute.util.View;
import vn.edu.hcmute.service.IUserService;
import vn.edu.hcmute.service.UserServiceImpl;
import vn.edu.hcmute.util.Flash;

/** Buoc 1 cua quen mat khau: nhap email de nhan OTP. */
@WebServlet("/forgot")
public class ForgotServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        View.render(req, resp, "/views/auth/forgot.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        try {
            boolean sent = userService.requestPasswordReset(email);
            Flash.success(req, sent
                    ? "Đã gửi mã xác nhận tới " + email + ". Nhập mã ở bước tiếp theo."
                    : "Đã tạo mã xác nhận. Chưa cấu hình SMTP nên mã được in ra console Tomcat.");
            resp.sendRedirect(req.getContextPath() + "/reset?email="
                    + URLEncoder.encode(email == null ? "" : email.trim(), StandardCharsets.UTF_8));
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            View.render(req, resp, "/views/auth/forgot.jsp");
        }
    }
}
