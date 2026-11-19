package vn.edu.hcmute.service;

import java.util.List;

import vn.edu.hcmute.dao.CategoryDao;
import vn.edu.hcmute.dao.ICategoryDao;
import vn.edu.hcmute.entity.Category;
import vn.edu.hcmute.util.FormValidation;

/**
 * Tang Service: chua nghiep vu, goi xuong DAO.
 * Controller chi duoc phep goi Service, khong goi thang DAO.
 */
public class CategoryServiceImpl implements ICategoryService {

    private final ICategoryDao cateDao = new CategoryDao();

    @Override
    public void insert(Category category) {
        validate(category);
        Category cate = this.findByCategoryname(category.getCategoryname());
        if (cate != null) {
            throw new IllegalArgumentException("Tên danh mục \"" + category.getCategoryname() + "\" đã tồn tại");
        }
        cateDao.insert(category);
    }

    @Override
    public void update(Category category) {
        validate(category);
        Category cate = this.findById(category.getCategoryid());
        if (cate == null) {
            throw new IllegalArgumentException("Không tìm thấy danh mục cần cập nhật");
        }
        Category trungTen = this.findByCategoryname(category.getCategoryname());
        if (trungTen != null && trungTen.getCategoryid() != category.getCategoryid()) {
            throw new IllegalArgumentException("Tên danh mục \"" + category.getCategoryname() + "\" đã tồn tại");
        }
        cateDao.update(category);
    }

    private void validate(Category category) {
        category.setCategoryname(FormValidation.required(category.getCategoryname(), "Tên danh mục", 50));
        if (category.getStatus() != 0 && category.getStatus() != 1) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }
    }

    @Override
    public void delete(int cateid) throws Exception {
        cateDao.delete(cateid);
    }

    @Override
    public Category findById(int cateid) {
        return cateDao.findById(cateid);
    }

    @Override
    public Category findByCategoryname(String name) {
        try {
            return cateDao.findByCategoryname(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Category> findAll() {
        return cateDao.findAll();
    }

    @Override
    public List<Category> findAll(int page, int pagesize) {
        return cateDao.findAll(page, pagesize);
    }

    @Override
    public List<Category> searchByName(String catname) {
        return cateDao.searchByName(catname);
    }

    @Override
    public int count() {
        return cateDao.count();
    }
}
