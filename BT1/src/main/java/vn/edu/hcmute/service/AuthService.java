package vn.edu.hcmute.service;

import vn.edu.hcmute.dao.UserDAO;
import vn.edu.hcmute.model.User;

/**
 * Tang SERVICE - xu ly nghiep vu dang nhap.
 * Controller KHONG duoc goi thang UserDAO, phai di qua day.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Kiem tra thong tin dang nhap.
     *
     * @return User neu dung ca username va password, null neu sai
     */
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        if (username.isBlank() || password.isEmpty()) {
            return null;
        }
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }
        return user.getPassword().equals(password) ? user : null;
    }
}
