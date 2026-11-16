package vn.edu.hcmute.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmute.util.Constants;

/**
 * Servlet phuc vu anh da upload: /image?fname=1234567.jpg
 * Can thiet vi trinh duyet khong the doc file ngoai webapp.
 */
@WebServlet(urlPatterns = { "/image" })
public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fname = req.getParameter("fname");

        if (fname == null || fname.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thieu tham so fname");
            return;
        }

        // Chan path traversal: chi cho phep ten file don gian
        if (fname.contains("..") || fname.contains("/") || fname.contains("\\")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ten file khong hop le");
            return;
        }

        Path file = Paths.get(Constants.DIR, fname);
        File f = file.toFile();
        if (!f.exists() || !f.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Khong tim thay anh: " + fname);
            return;
        }

        String contentType = getServletContext().getMimeType(fname);
        resp.setContentType(contentType != null ? contentType : "application/octet-stream");
        resp.setContentLengthLong(f.length());
        resp.setHeader("Cache-Control", "public, max-age=86400");
        Files.copy(file, resp.getOutputStream());
    }
}
