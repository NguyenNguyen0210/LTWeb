package vn.edu.hcmute.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmute.entity.User;
import vn.edu.hcmute.util.Flash;

/**
 * Chan truy cap:
 *  - /admin/*        : phai dang nhap VA co role = admin
 *  - /video, /video/*: phai dang nhap (rieng /video/detail cho phep xem tu trang chu)
 */
@WebFilter(urlPatterns = { "/admin/*", "/video", "/video/*" })
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");

        boolean publicVideoDetail = "/video/detail".equals(path);

        if (user == null) {
            if (publicVideoDetail) {
                chain.doFilter(request, response);
                return;
            }
            // Nho trang dang muon vao de quay lai sau khi dang nhap
            String target = path + (req.getQueryString() == null ? "" : "?" + req.getQueryString());
            req.getSession(true).setAttribute("redirectAfterLogin", target);
            Flash.error(req, "Vui lòng đăng nhập để tiếp tục");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (path.startsWith("/admin") && !user.isAdmin()) {
            Flash.error(req, "Bạn không có quyền truy cập trang quản trị");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        chain.doFilter(request, response);
    }
}
