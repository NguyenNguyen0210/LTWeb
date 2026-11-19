package vn.edu.hcmute.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.hcmute.util.View;
import vn.edu.hcmute.entity.Category;
import vn.edu.hcmute.service.CategoryServiceImpl;
import vn.edu.hcmute.service.ICategoryService;
import vn.edu.hcmute.util.Constants;
import vn.edu.hcmute.util.FormValidation;
import vn.edu.hcmute.util.UploadUtil;

@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
@WebServlet(urlPatterns = { "/admin/categories", "/admin/category/add", "/admin/category/insert",
        "/admin/category/edit", "/admin/category/update", "/admin/category/delete" })
public class CategoryController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public ICategoryService cateService = new CategoryServiceImpl();

    // ===================== GET =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getServletPath();

        if (url.equals("/admin/categories")) {
            showList(req, resp);

        } else if (url.equals("/admin/category/add")) {
            View.render(req, resp, "/views/admin/category-add.jsp");

        } else if (url.equals("/admin/category/edit")) {
            Integer id = parseInt(req.getParameter("id"));
            if (id == null) {
                flash(req, "error", "Id không hợp lệ");
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }
            Category category = cateService.findById(id);
            if (category == null) {
                flash(req, "error", "Không tìm thấy danh mục có id = " + id);
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }
            req.setAttribute("cate", category);
            View.render(req, resp, "/views/admin/category-edit.jsp");

        } else if (url.equals("/admin/category/delete")) {
            Integer id = parseInt(req.getParameter("id"));
            if (id == null) {
                flash(req, "error", "Id không hợp lệ");
            } else {
                try {
                    cateService.delete(id);
                    flash(req, "success", "Đã xóa danh mục #" + id);
                } catch (Exception e) {
                    // Thuong gap: danh muc dang duoc Video tham chieu (FK constraint)
                    flash(req, "error", "Không xóa được danh mục #" + id + ": " + e.getMessage());
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        } else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    // ===================== POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getServletPath();

        try {
            if (url.equals("/admin/category/insert")) {
                handleInsert(req, resp);
            } else if (url.equals("/admin/category/update")) {
                handleUpdate(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ảnh không được vượt quá 5 MB");
        }
    }

    // ===================== NGHIEP VU =====================

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        int page = 0;
        Integer p = parseInt(req.getParameter("page"));
        if (p != null && p > 0) {
            page = p - 1; // nguoi dung nhin thay trang 1,2,3...; DAO tinh tu 0
        }

        List<Category> list;
        int total;

        if (keyword != null && !keyword.isBlank()) {
            keyword = keyword.trim();
            list = cateService.searchByName(keyword); // tim kiem: tra het ket qua
            total = list.size();
        } else {
            keyword = null;
            total = cateService.count();
            int totalPages = (int) Math.ceil((double) total / Constants.PAGE_SIZE);
            if (totalPages > 0 && page >= totalPages) {
                page = totalPages - 1;
            }
            list = cateService.findAll(page, Constants.PAGE_SIZE);
        }

        int totalPages = (int) Math.ceil((double) total / Constants.PAGE_SIZE);

        req.setAttribute("listcate", list);
        req.setAttribute("keyword", keyword);
        req.setAttribute("total", total);
        req.setAttribute("currentPage", page + 1);
        req.setAttribute("totalPages", totalPages);
        View.render(req, resp, "/views/admin/category-list.jsp");
    }

    private void handleInsert(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("categoryname");
        String imageLink = req.getParameter("images");
        String status = req.getParameter("status");
        String uploaded = null;
        try {
            Category category = new Category();
            category.setCategoryname(FormValidation.required(name, "Tên danh mục", 50));
            category.setStatus(FormValidation.state(status, 1));
            uploaded = UploadUtil.save(req.getPart("images1"), UploadUtil.IMAGE_EXT, "Ảnh danh mục");
            String image = uploaded != null ? uploaded : FormValidation.mediaLink(imageLink, null, "Link ảnh");
            category.setImages(image == null ? Constants.DEFAULT_IMAGE : image);
            cateService.insert(category);
        } catch (IllegalArgumentException e) {
            UploadUtil.deleteQuietly(uploaded);
            forwardAdd(req, resp, e.getMessage(), name, imageLink, parseInt(status));
            return;
        } catch (Exception e) {
            UploadUtil.deleteQuietly(uploaded);
            log("Không thêm được danh mục", e);
            forwardAdd(req, resp, "Không thể thêm danh mục. Vui lòng thử lại.", name, imageLink, parseInt(status));
            return;
        }
        flash(req, "success", "Đã thêm danh mục");
        resp.sendRedirect(req.getContextPath() + "/admin/categories");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer id = parseInt(req.getParameter("categoryid"));
        Category category = id == null ? null : cateService.findById(id);
        if (category == null) {
            flash(req, "error", "Không tìm thấy danh mục cần cập nhật");
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        String oldImage = category.getImages();
        String name = req.getParameter("categoryname");
        String imageLink = req.getParameter("images");
        String uploaded = null;
        try {
            category.setCategoryname(FormValidation.required(name, "Tên danh mục", 50));
            category.setStatus(FormValidation.state(req.getParameter("status"), category.getStatus()));
            uploaded = UploadUtil.save(req.getPart("images1"), UploadUtil.IMAGE_EXT, "Ảnh danh mục");
            String image = uploaded != null ? uploaded : FormValidation.mediaLink(imageLink, oldImage, "Link ảnh");
            category.setImages(image == null ? oldImage : image);
            cateService.update(category);
        } catch (IllegalArgumentException e) {
            UploadUtil.deleteQuietly(uploaded);
            forwardEdit(req, resp, category, name, imageLink, oldImage, e.getMessage());
            return;
        } catch (Exception e) {
            UploadUtil.deleteQuietly(uploaded);
            log("Không cập nhật được danh mục", e);
            forwardEdit(req, resp, category, name, imageLink, oldImage, "Không thể cập nhật danh mục. Vui lòng thử lại.");
            return;
        }

        if (!java.util.Objects.equals(oldImage, category.getImages())) {
            UploadUtil.deleteQuietly(oldImage);
        }
        flash(req, "success", "Đã cập nhật danh mục");
        resp.sendRedirect(req.getContextPath() + "/admin/categories");
    }

    private void forwardEdit(HttpServletRequest req, HttpServletResponse resp, Category category,
                             String name, String imageLink, String oldImage, String error)
            throws ServletException, IOException {
        category.setImages(oldImage);
        req.setAttribute("cate", category);
        req.setAttribute("categoryname", name);
        req.setAttribute("images", imageLink);
        req.setAttribute("error", error);
        View.render(req, resp, "/views/admin/category-edit.jsp");
    }

    private void forwardAdd(HttpServletRequest req, HttpServletResponse resp, String error,
                            String categoryname, String images, Integer status) throws ServletException, IOException {
        req.setAttribute("error", error);
        req.setAttribute("categoryname", categoryname);
        req.setAttribute("images", images);
        req.setAttribute("status", status);
        View.render(req, resp, "/views/admin/category-add.jsp");
    }

    /** Thong bao 1 lan (flash) - hien o trang sau khi redirect roi tu xoa. */
    private void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flashMsg", message);
    }

    private static Integer parseInt(String s) {
        try {
            return s == null ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
