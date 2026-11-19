package vn.edu.hcmute.util;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class View {

    private View() {
    }

    public static void render(HttpServletRequest request, HttpServletResponse response, String jsp)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        // Tomcat 11 commits a forwarded response before SiteMesh can write the decorated HTML.
        request.getRequestDispatcher(jsp).include(request, response);
    }
}
