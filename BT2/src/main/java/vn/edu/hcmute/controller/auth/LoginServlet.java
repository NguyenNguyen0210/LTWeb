package vn.edu.hcmute.controller.auth;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmute.entity.User;
import vn.edu.hcmute.service.IUserService;
import vn.edu.hcmute.service.UserServiceImpl;
import vn.edu.hcmute.util.Flash;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) != null && req.getSession(false).getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = userService.login(email, password);

            // Chong session fixation: huy session cu, tao session moi sau khi dang nhap thanh cong
            HttpSession oldSession = req.getSession(false);
            String redirect = null;
            if (oldSession != null) {
                redirect = (String) oldSession.getAttribute("redirectAfterLogin");
                oldSession.invalidate();
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60);

            Flash.success(req, "Xin chào " + user.getDisplayName() + "!");
            resp.sendRedirect(req.getContextPath() + (redirect != null ? redirect : "/home"));

        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
        }
    }
}
