package vn.edu.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmute.model.User;
import vn.edu.hcmute.service.AuthService;

import java.io.IOException;

/**
 * URL "/login":
 *  - GET  : hien thi form dang nhap (login.jsp)
 *  - POST : xu ly nut Login -> thanh cong thi /home, that bai thi /error
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Ho tro nut "Dang xuat": /login?action=logout
        if ("logout".equals(request.getParameter("action"))) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Bat buoc: form gui bang UTF-8 de khong loi tieng Viet
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = authService.login(username, password);

        if (user != null) {
            // Dang nhap thanh cong -> luu session -> chuyen sang /home
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("loginTime", System.currentTimeMillis());
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            // Dang nhap that bai -> chuyen sang /error
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
