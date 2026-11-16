package vn.edu.hcmute.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.edu.hcmute.entity.Category;
import vn.edu.hcmute.service.CategoryServiceImpl;
import vn.edu.hcmute.service.ICategoryService;
import vn.edu.hcmute.util.Constants;

@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
@WebServlet(urlPatterns = { "/admin/categories", "/admin/category/add", "/admin/category/insert",
        "/admin/category/edit", "/admin/category/update", "/admin/category/delete" })
public class CategoryController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");

    public ICategoryService cateService = new CategoryServiceImpl();

    // ===================== GET =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();

        if (url.contains("/admin/categories")) {
            showList(req, resp);

        } else if (url.contains("/admin/category/add")) {
            req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);

        } else if (url.contains("/admin/category/edit")) {
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
            req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);

        } else { // /admin/category/delete
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
        }
    }

    // ===================== POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();

        if (url.contains("/admin/category/insert")) {
            handleInsert(req, resp);

        } else if (url.contains("/admin/category/update")) {
            handleUpdate(req, resp);
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
        req.getRequestDispatcher("/views/admin/category-list.jsp").forward(req, resp);
    }

    private void handleInsert(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String categoryname = trim(req.getParameter("categoryname"));
        String images = trim(req.getParameter("images"));
        Integer status = parseInt(req.getParameter("status"));

        if (categoryname == null) {
            forwardAdd(req, resp, "Tên danh mục không được để trống", categoryname, images, status);
            return;
        }

        Category category = new Category();
        category.setCategoryname(categoryname);
        category.setStatus(status != null ? status : 1);

        try {
            String uploaded = saveUpload(req);
            if (uploaded != null) {
                category.setImages(uploaded);
            } else if (images != null) {
                category.setImages(images);
            } else {
                category.setImages(Constants.DEFAULT_IMAGE);
            }

            cateService.insert(category);
            flash(req, "success", "Đã thêm danh mục \"" + categoryname + "\"");
            resp.sendRedirect(req.getContextPath() + "/admin/categories");

        } catch (IllegalArgumentException e) {
            forwardAdd(req, resp, e.getMessage(), categoryname, images, status);
        } catch (Exception e) {
            e.printStackTrace();
            forwardAdd(req, resp, "Lỗi khi thêm: " + e.getMessage(), categoryname, images, status);
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer categoryid = parseInt(req.getParameter("categoryid"));
        String categoryname = trim(req.getParameter("categoryname"));
        String images = trim(req.getParameter("images"));
        Integer status = parseInt(req.getParameter("status"));

        if (categoryid == null) {
            flash(req, "error", "Id không hợp lệ");
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        Category category = cateService.findById(categoryid);
        if (category == null) {
            flash(req, "error", "Không tìm thấy danh mục có id = " + categoryid);
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        if (categoryname == null) {
            req.setAttribute("cate", category);
            req.setAttribute("error", "Tên danh mục không được để trống");
            req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
            return;
        }

        String fileold = category.getImages();
        category.setCategoryname(categoryname);
        category.setStatus(status != null ? status : category.getStatus());

        try {
            String uploaded = saveUpload(req);
            if (uploaded != null) {
                // Co anh moi -> xoa file cu tren dia (chi xoa khi la file upload, khong xoa URL)
                if (fileold != null && !fileold.startsWith("http")) {
                    deleteFile(Paths.get(Constants.DIR, fileold).toString());
                }
                category.setImages(uploaded);
            } else if (images != null) {
                category.setImages(images);
            } else {
                category.setImages(fileold);
            }

            cateService.update(category);
            flash(req, "success", "Đã cập nhật danh mục \"" + categoryname + "\"");
            resp.sendRedirect(req.getContextPath() + "/admin/categories");

        } catch (IllegalArgumentException e) {
            req.setAttribute("cate", category);
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("cate", category);
            req.setAttribute("error", "Lỗi khi cập nhật: " + e.getMessage());
            req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
        }
    }

    // ===================== TIEN ICH =====================

    /**
     * Luu file upload vao Constants.DIR, tra ve ten file moi (da doi ten bang
     * timestamp de khong trung), hoac null neu nguoi dung khong chon file.
     */
    private String saveUpload(HttpServletRequest req) throws IOException, ServletException {
        Part part;
        try {
            part = req.getPart("images1");
        } catch (IllegalStateException e) {
            throw new IOException("File upload quá lớn (tối đa 5MB)", e);
        }

        if (part == null || part.getSize() == 0) {
            return null;
        }

        String submitted = part.getSubmittedFileName();
        if (submitted == null || submitted.isBlank()) {
            return null;
        }

        String filename = Paths.get(submitted).getFileName().toString();
        int index = filename.lastIndexOf('.');
        String ext = index >= 0 ? filename.substring(index + 1).toLowerCase(Locale.ROOT) : "";

        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh: " + String.join(", ", ALLOWED_EXT));
        }

        File uploadDir = new File(Constants.DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fname = System.currentTimeMillis() + "." + ext;
        part.write(Constants.DIR + File.separator + fname);
        return fname;
    }

    private void forwardAdd(HttpServletRequest req, HttpServletResponse resp, String error,
                            String categoryname, String images, Integer status) throws ServletException, IOException {
        req.setAttribute("error", error);
        req.setAttribute("categoryname", categoryname);
        req.setAttribute("images", images);
        req.setAttribute("status", status);
        req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
    }

    public static void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Khong xoa duoc file cu cung khong nen lam hong thao tac update
            e.printStackTrace();
        }
    }

    /** Thong bao 1 lan (flash) - hien o trang sau khi redirect roi tu xoa. */
    private void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flashMsg", message);
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
            return s == null ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
