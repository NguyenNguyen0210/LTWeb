package vn.edu.hcmute.dao;

import java.util.List;

import vn.edu.hcmute.entity.User;

public interface IUserDao {

    void insert(User user);

    void update(User user);

    void delete(int userid) throws Exception;

    User findById(int userid);

    /** Tim theo email (email la duy nhat). Khong thay -> null. */
    User findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAll();

    int count();

    boolean updateProfileDetails(int userId, String fullname, String phone, String avatar);

    boolean changeEmail(int userId, String currentEmail, String newEmail);
}
