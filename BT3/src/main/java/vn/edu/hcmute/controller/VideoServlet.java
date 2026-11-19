package vn.edu.hcmute.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
import vn.edu.hcmute.util.FormValidation;
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
                    videoService.delete(video.getVideoId());
                    UploadUtil.deleteQuietly(video.getPoster());
                    UploadUtil.deleteQuietly(video.getVideoUrl());
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
        try {
            if (url.endsWith("/video/insert")) {
                handleInsert(req, resp);
            } else if (url.endsWith("/video/update")) {
                handleUpdate(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tệp video không được vượt quá 100 MB");
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
        Video video = new Video();
        video.setVideoId(generateId());
        video.setActive(1);
        video.setCreatedAt(LocalDateTime.now());
        video.setUser(currentUser(req));
        save(req, resp, video, false);
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Video video = videoService.findById(req.getParameter("videoId"));
        if (video == null || !canEdit(video, currentUser(req))) {
            Flash.error(req, video == null ? "Không tìm thấy video" : "Bạn không có quyền sửa video này");
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }
        save(req, resp, video, true);
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, Video video, boolean editing)
            throws ServletException, IOException {
        String view = editing ? "/views/video/edit.jsp" : "/views/video/add.jsp";
        String oldVideo = video.getVideoUrl();
        String oldPoster = video.getPoster();
        String videoLink = req.getParameter("videoUrl");
        String posterLink = req.getParameter("poster");
        String videoFile = null;
        String posterFile = null;
        video.setTitle(req.getParameter("title"));
        video.setDescription(req.getParameter("description"));

        try {
            video.setTitle(FormValidation.required(video.getTitle(), "Tiêu đề video", 200));
            video.setDescription(FormValidation.optional(video.getDescription(), "Mô tả", 500));
            video.setActive(FormValidation.state(req.getParameter("active"), video.getActive()));
            video.setCategory(findCategory(req.getParameter("categoryId")));

            videoFile = UploadUtil.save(req.getPart("videoFile"), UploadUtil.VIDEO_EXT, "File video");
            posterFile = UploadUtil.save(req.getPart("posterFile"), UploadUtil.IMAGE_EXT, "Ảnh poster");
            video.setVideoUrl(videoFile != null ? videoFile : FormValidation.mediaLink(videoLink, oldVideo, "Link video"));
            video.setPoster(posterFile != null ? posterFile : FormValidation.mediaLink(posterLink, oldPoster, "Link poster"));

            if (editing) {
                videoService.update(video);
            } else {
                videoService.insert(video);
            }
        } catch (IllegalArgumentException e) {
            invalidForm(req, resp, video, view, videoFile, posterFile, videoLink, posterLink, oldVideo, oldPoster, e.getMessage());
            return;
        } catch (Exception e) {
            log("Không lưu được video", e);
            invalidForm(req, resp, video, view, videoFile, posterFile, videoLink, posterLink, oldVideo, oldPoster,
                    "Không thể lưu video. Hãy kiểm tra kích thước tệp và thử lại.");
            return;
        }

        if (!Objects.equals(oldVideo, video.getVideoUrl())) {
            UploadUtil.deleteQuietly(oldVideo);
        }
        if (!Objects.equals(oldPoster, video.getPoster())) {
            UploadUtil.deleteQuietly(oldPoster);
        }
        Flash.success(req, editing ? "Đã cập nhật video" : "Đã thêm video");
        resp.sendRedirect(req.getContextPath() + (editing ? "/video/detail?id=" + video.getVideoId() : "/video"));
    }

    private void invalidForm(HttpServletRequest req, HttpServletResponse resp, Video video, String view,
                             String videoFile, String posterFile, String videoLink, String posterLink,
                             String oldVideo, String oldPoster, String error) throws ServletException, IOException {
        UploadUtil.deleteQuietly(videoFile);
        UploadUtil.deleteQuietly(posterFile);
        video.setVideoUrl(oldVideo);
        video.setPoster(oldPoster);
        req.setAttribute("submittedVideoUrl", videoLink);
        req.setAttribute("submittedPoster", posterLink);
        forwardForm(req, resp, view, error, video);
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
        if (categoryId == null || categoryId.isBlank()) {
            return null;
        }
        Integer id = parseInt(categoryId);
        Category category = id == null || id <= 0 ? null : categoryService.findById(id);
        if (category == null) {
            throw new IllegalArgumentException("Danh mục không hợp lệ");
        }
        return category;
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
}
