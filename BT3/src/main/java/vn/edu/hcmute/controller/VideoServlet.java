package vn.edu.hcmute.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmute.util.View;
import vn.edu.hcmute.entity.Category;
import vn.edu.hcmute.entity.User;
import vn.edu.hcmute.entity.Video;
import vn.edu.hcmute.service.CategoryServiceImpl;
import vn.edu.hcmute.service.ICategoryService;
import vn.edu.hcmute.service.IVideoService;
import vn.edu.hcmute.service.VideoServiceImpl;
import vn.edu.hcmute.util.Constants;
import vn.edu.hcmute.util.Flash;
import vn.edu.hcmute.util.UploadUtil;

/**
 * Quan ly video cua user dang dang nhap.
 *
 *  GET  /video                 danh sach video cua user, 6 video/trang
 *  GET  /video/add             form them
 *  GET  /video/edit?id=        form sua
 *  GET  /video/detail?id=      xem chi tiet + play (cho phep ca khi chua dang nhap)
 *  GET  /video/delete?id=      xoa
 *  POST /video/insert          them moi
 *  POST /video/update          cap nhat
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 100L * 1024 * 1024,
        maxRequestSize = 110L * 1024 * 1024)
@WebServlet(urlPatterns = { "/video", "/video/add", "/video/edit", "/video/insert",
        "/video/update", "/video/delete", "/video/detail" })
public class VideoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IVideoService videoService = new VideoServiceImpl();
    private final ICategoryService categoryService = new CategoryServiceImpl();

    // ===================== GET =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();

        if (url.endsWith("/video/detail")) {
            showDetail(req, resp);

        } else if (url.endsWith("/video/add")) {
            req.setAttribute("categories", categoryService.findAll());
            View.render(req, resp, "/views/video/add.jsp");

        } else if (url.endsWith("/video/edit")) {
            User user = currentUser(req);
            Video video = videoService.findById(req.getParameter("id"));
            if (video == null) {
                Flash.error(req, "Không tìm thấy video");
                resp.sendRedirect(req.getContextPath() + "/video");
                return;
            }
            if (!canEdit(video, user)) {
                Flash.error(req, "Bạn không có quyền sửa video này");
                resp.sendRedirect(req.getContextPath() + "/video");
                return;
            }
            req.setAttribute("video", video);
            req.setAttribute("categories", categoryService.findAll());
            View.render(req, resp, "/views/video/edit.jsp");

        } else if (url.endsWith("/video/delete")) {
            User user = currentUser(req);
            Video video = videoService.findById(req.getParameter("id"));
            if (video == null) {
                Flash.error(req, "Không tìm thấy video");
            } else if (!canEdit(video, user)) {
                Flash.error(req, "Bạn không có quyền xóa video này");
            } else {
                try {
                    UploadUtil.deleteQuietly(video.getPoster());
                    UploadUtil.deleteQuietly(video.getVideoUrl());
                    videoService.delete(video.getVideoId());
                    Flash.success(req, "Đã xóa video \"" + video.getTitle() + "\"");
                } catch (Exception e) {
                    Flash.error(req, "Không xóa được video: " + e.getMessage());
                }
            }
            resp.sendRedirect(req.getContextPath() + "/video");

        } else {
            showList(req, resp);
        }
    }

    // ===================== POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        if (url.endsWith("/video/insert")) {
            handleInsert(req, resp);
        } else if (url.endsWith("/video/update")) {
            handleUpdate(req, resp);
        }
    }

    // ===================== XU LY =====================

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        String keyword = trim(req.getParameter("keyword"));
        int page = 0;
        Integer p = parseInt(req.getParameter("page"));
        if (p != null && p > 0) {
            page = p - 1;
        }

        List<Video> list;
        int total;

        if (keyword != null) {
            list = videoService.searchByUser(user.getUserid(), keyword);
            total = list.size();
        } else {
            total = videoService.countByUser(user.getUserid());
            int totalPages = (int) Math.ceil((double) total / Constants.VIDEO_PAGE_SIZE);
            if (totalPages > 0 && page >= totalPages) {
                page = totalPages - 1;
            }
            list = videoService.findByUser(user.getUserid(), page, Constants.VIDEO_PAGE_SIZE);
        }

        int totalPages = (int) Math.ceil((double) total / Constants.VIDEO_PAGE_SIZE);

        req.setAttribute("listvideo", list);
        req.setAttribute("keyword", keyword);
        req.setAttribute("total", total);
        req.setAttribute("currentPage", page + 1);
        req.setAttribute("totalPages", totalPages);
        View.render(req, resp, "/views/video/list.jsp");
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Video video = videoService.findById(req.getParameter("id"));
        if (video == null) {
            Flash.error(req, "Không tìm thấy video");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        videoService.increaseViews(video.getVideoId());
        video.setViews(video.getViews() + 1);

        req.setAttribute("video", video);
        req.setAttribute("canEdit", canEdit(video, currentUser(req)));
        View.render(req, resp, "/views/video/detail.jsp");
    }

    private void handleInsert(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        String title = trim(req.getParameter("title"));

        if (title == null) {
            forwardForm(req, resp, "/views/video/add.jsp", "Tiêu đề video không được để trống", null);
            return;
        }

        Video video = new Video();
        video.setVideoId(generateId());
        video.setTitle(title);
        video.setDescription(trim(req.getParameter("description")));
        video.setActive(parseIntOr(req.getParameter("active"), 1));
        video.setViews(0);
        video.setCreatedAt(LocalDateTime.now());
        video.setUser(user);
        video.setCategory(findCategory(req.getParameter("categoryId")));

        try {
            String videoFile = UploadUtil.save(req.getPart("videoFile"), UploadUtil.VIDEO_EXT, "File video");
            String posterFile = UploadUtil.save(req.getPart("posterFile"), UploadUtil.IMAGE_EXT, "Ảnh poster");

            if (videoFile != null) {
                video.setVideoUrl(videoFile);
            } else {
                video.setVideoUrl(trim(req.getParameter("videoUrl")));
            }

            if (posterFile != null) {
                video.setPoster(posterFile);
            } else {
                video.setPoster(trim(req.getParameter("poster")));
            }

            videoService.insert(video);
            Flash.success(req, "Đã thêm video \"" + title + "\"");
            resp.sendRedirect(req.getContextPath() + "/video");

        } catch (IllegalArgumentException e) {
            forwardForm(req, resp, "/views/video/add.jsp", e.getMessage(), video);
        } catch (Exception e) {
            e.printStackTrace();
            forwardForm(req, resp, "/views/video/add.jsp", "Lỗi khi thêm video: " + e.getMessage(), video);
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        Video video = videoService.findById(req.getParameter("videoId"));

        if (video == null) {
            Flash.error(req, "Không tìm thấy video");
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }
        if (!canEdit(video, user)) {
            Flash.error(req, "Bạn không có quyền sửa video này");
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }

        String title = trim(req.getParameter("title"));
        if (title == null) {
            forwardForm(req, resp, "/views/video/edit.jsp", "Tiêu đề video không được để trống", video);
            return;
        }

        String oldPoster = video.getPoster();
        String oldVideo = video.getVideoUrl();

        video.setTitle(title);
        video.setDescription(trim(req.getParameter("description")));
        video.setActive(parseIntOr(req.getParameter("active"), video.getActive()));
        video.setCategory(findCategory(req.getParameter("categoryId")));

        try {
            String videoFile = UploadUtil.save(req.getPart("videoFile"), UploadUtil.VIDEO_EXT, "File video");
            String posterFile = UploadUtil.save(req.getPart("posterFile"), UploadUtil.IMAGE_EXT, "Ảnh poster");

            if (videoFile != null) {
                UploadUtil.deleteQuietly(oldVideo);
                video.setVideoUrl(videoFile);
            } else {
                String urlParam = trim(req.getParameter("videoUrl"));
                video.setVideoUrl(urlParam);
            }

            if (posterFile != null) {
                UploadUtil.deleteQuietly(oldPoster);
                video.setPoster(posterFile);
            } else {
                video.setPoster(trim(req.getParameter("poster")));
            }

            videoService.update(video);
            Flash.success(req, "Đã cập nhật video \"" + title + "\"");
            resp.sendRedirect(req.getContextPath() + "/video/detail?id=" + video.getVideoId());

        } catch (IllegalArgumentException e) {
            forwardForm(req, resp, "/views/video/edit.jsp", e.getMessage(), video);
        } catch (Exception e) {
            e.printStackTrace();
            forwardForm(req, resp, "/views/video/edit.jsp", "Lỗi khi cập nhật: " + e.getMessage(), video);
        }
    }

    // ===================== TIEN ICH =====================

    private void forwardForm(HttpServletRequest req, HttpServletResponse resp, String view, String error, Video video)
            throws ServletException, IOException {
        req.setAttribute("error", error);
        if (video != null) {
            req.setAttribute("video", video);
        }
        req.setAttribute("categories", categoryService.findAll());
        View.render(req, resp, view);
    }

    private Category findCategory(String categoryId) {
        Integer id = parseInt(categoryId);
        return id == null ? null : categoryService.findById(id);
    }

    private static boolean canEdit(Video video, User user) {
        if (user == null || video == null || video.getUser() == null) {
            return false;
        }
        return user.isAdmin() || video.getUser().getUserid() == user.getUserid();
    }

    private static User currentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("user");
    }

    private static String generateId() {
        return "v" + System.currentTimeMillis() + (100 + (int) (Math.random() * 900));
    }

    private static String trim(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static Integer parseInt(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static int parseIntOr(String s, int def) {
        Integer v = parseInt(s);
        return v == null ? def : v;
    }
}
