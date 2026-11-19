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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        View.render(req, resp, "/views/auth/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String fullname = req.getParameter("fullname");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");

        try {
            boolean sent = userService.register(email, fullname, password, confirm);

            String msg = sent
                    ? "Đã gửi mã OTP 6 số tới " + email + ". Mã có hiệu lực 5 phút."
                    : "Tài khoản đã được tạo. Chưa cấu hình SMTP nên mã OTP được in ra console Tomcat.";
            Flash.success(req, msg);

            String target = "/verify?email=" + URLEncoder.encode(email == null ? "" : email.trim(),
                    StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + target);

        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            req.setAttribute("fullname", fullname);
            View.render(req, resp, "/views/auth/register.jsp");
        }
    }
}
