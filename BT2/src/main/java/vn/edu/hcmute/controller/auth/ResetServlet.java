package vn.edu.hcmute.controller.auth;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmute.service.IUserService;
import vn.edu.hcmute.service.MailService;
import vn.edu.hcmute.service.UserServiceImpl;
import vn.edu.hcmute.util.Flash;

/** Buoc 2 cua quen mat khau: nhap OTP + mat khau moi. */
@WebServlet("/reset")
public class ResetServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || email.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/forgot");
            return;
        }
        forward(req, resp, email, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String otp = req.getParameter("otp");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");

        if (email == null || email.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/forgot");
            return;
        }

        try {
            userService.resetPassword(email, otp, password, confirm);
            Flash.success(req, "Đặt lại mật khẩu thành công. Hãy đăng nhập bằng mật khẩu mới.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (RuntimeException e) {
            forward(req, resp, email, e.getMessage());
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String email, String error)
            throws ServletException, IOException {
        req.setAttribute("email", email);
        req.setAttribute("mailConfigured", MailService.isConfigured());
        if (error != null) {
            req.setAttribute("error", error);
        }
        req.getRequestDispatcher("/views/auth/reset.jsp").forward(req, resp);
    }
}
