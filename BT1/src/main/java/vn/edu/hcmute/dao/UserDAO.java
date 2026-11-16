package vn.edu.hcmute.dao;

import vn.edu.hcmute.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Tang DAO (Data Access Object) - noi duy nhat truy cap du lieu.
 * Hien tai dung List trong bo nho cho gon; khi can co the thay bang JDBC
 * ma KHONG phai sua Service hay Controller.
 */
public class UserDAO {

    private static final List<User> USERS = new ArrayList<>();

    static {
        USERS.add(new User("admin", "123456", "Quan tri vien"));
        USERS.add(new User("student", "123456", "Sinh vien HCMUTE"));
    }

    /**
     * Tim nguoi dung theo username (khong phan biet khoang trang dau cuoi).
     *
     * @return User neu tim thay, null neu khong co
     */
    public User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        String key = username.trim();
        for (User user : USERS) {
            if (user.getUsername().equalsIgnoreCase(key)) {
                return user;
            }
        }
        return null;
    }
}
