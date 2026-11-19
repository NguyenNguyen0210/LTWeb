package vn.edu.hcmute.service;

import java.util.List;

import vn.edu.hcmute.entity.User;

public interface IUserService {

    User findById(int userid);

    User findByEmail(String email);

    List<User> findAll();

    int count();

    /**
     * Dang ky tai khoan moi (status = chua kich hoat) va gui OTP qua email.
     *
     * @return true neu gui duoc mail that, false neu dang o che do DEV (OTP in ra console)
     * @throws IllegalArgumentException khi du lieu khong hop le / email da ton tai
     */
    boolean register(String email, String fullname, String rawPassword, String confirmPassword);

    /** Kich hoat tai khoan bang OTP. */
    void activate(String email, String otp);

    /** Gui lai OTP kich hoat. */
    boolean resendActivationOtp(String email);

    /**
     * Dang nhap.
     *
     * @throws IllegalArgumentException sai email/mat khau
     * @throws IllegalStateException    tai khoan chua kich hoat hoac bi khoa
     */
    User login(String email, String rawPassword);

    /** Quen mat khau: sinh OTP va gui mail. */
    boolean requestPasswordReset(String email);

    /** Dat lai mat khau bang OTP. */
    void resetPassword(String email, String otp, String newPassword, String confirmPassword);

    /** Cap nhat thong tin ca nhan (khong doi mat khau). */
    void updateProfile(User user);
}
