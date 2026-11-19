package vn.edu.hcmute.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmute.util.Constants;

/**
 * Phuc vu file da upload (anh va video) nam ngoai webapp.
 *   /image?fname=abc.jpg   - anh (dung o trang category, trang chu)
 *   /media?fname=abc.mp4   - video (dung o trang play)
 *
 * Ho tro HTTP Range de tua (seek) video duoc, va de trinh duyet chi tai
 * tung doan thay vi tai ca file.
 */
@WebServlet(urlPatterns = { "/image", "/media" })
public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fname = req.getParameter("fname");

        if (fname == null || fname.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số fname");
            return;
        }

        // Chan path traversal: chi cho phep ten file don gian trong Constants.DIR
        if (fname.contains("..") || fname.contains("/") || fname.contains("\\")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tên file không hợp lệ");
            return;
        }

        File f = new File(Constants.DIR, fname);
        if (!f.exists() || !f.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy file: " + fname);
            return;
        }

        String contentType = getServletContext().getMimeType(fname);
        resp.setContentType(contentType != null ? contentType : "application/octet-stream");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Accept-Ranges", "bytes");
        resp.setHeader("Cache-Control", "public, max-age=86400");

        long length = f.length();
        String range = req.getHeader("Range");

        if (range == null || !range.startsWith("bytes=")) {
            resp.setContentLengthLong(length);
            try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
                copy(raf, resp.getOutputStream(), 0, length);
            }
            return;
        }

        // Dang "bytes=start-end"
        long start = 0;
        long end = length - 1;
        try {
            String spec = range.substring(6).trim();
            int dash = spec.indexOf('-');
            if (dash > 0) {
                start = Long.parseLong(spec.substring(0, dash).trim());
            }
            if (dash >= 0 && dash < spec.length() - 1) {
                end = Long.parseLong(spec.substring(dash + 1).trim());
            }
        } catch (NumberFormatException e) {
            resp.setHeader("Content-Range", "bytes */" + length);
            resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
        }

        if (start < 0 || start >= length || end < start) {
            resp.setHeader("Content-Range", "bytes */" + length);
            resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
        }
        if (end > length - 1) {
            end = length - 1;
        }

        long len = end - start + 1;
        resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + length);
        resp.setContentLengthLong(len);

        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            copy(raf, resp.getOutputStream(), start, len);
        }
    }

    private void copy(RandomAccessFile raf, OutputStream os, long start, long len) throws IOException {
        raf.seek(start);
        byte[] buffer = new byte[16 * 1024];
        long remaining = len;
        while (remaining > 0) {
            int read = raf.read(buffer, 0, (int) Math.min(buffer.length, remaining));
            if (read < 0) {
                break;
            }
            os.write(buffer, 0, read);
            remaining -= read;
        }
        os.flush();
    }
}
