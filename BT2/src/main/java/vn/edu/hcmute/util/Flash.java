package vn.edu.hcmute.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Thong bao "flash": chi hien thi MOT lan o trang ke tiep sau khi redirect.
 * Luu vao session, JSP doc xong thi xoa (xem views/inc/head.jspf).
 */
public final class Flash {

    public static final String MSG = "flashMsg";
    public static final String TYPE = "flashType";

    private Flash() {
    }

    public static void success(HttpServletRequest req, String message) {
        set(req, "success", message);
    }

    public static void error(HttpServletRequest req, String message) {
        set(req, "error", message);
    }

    private static void set(HttpServletRequest req, String type, String message) {
        HttpSession session = req.getSession();
        session.setAttribute(TYPE, type);
        session.setAttribute(MSG, message);
    }
}
