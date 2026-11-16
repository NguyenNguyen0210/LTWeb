package vn.edu.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * URL "/error": trang bao dang nhap that bai.
 * Gui kem ma HTTP 401 (Unauthorized) cho dung nghia.
 */
@WebServlet(name = "ErrorServlet", urlPatterns = {"/error"})
public class ErrorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        request.getRequestDispatcher("/views/error.jsp").forward(request, response);
    }
}
