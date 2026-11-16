package vn.edu.hcmute.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import vn.edu.hcmute.dao.IUserDao;
import vn.edu.hcmute.dao.UserDao;
import vn.edu.hcmute.entity.User;
import vn.edu.hcmute.util.Constants;
import vn.edu.hcmute.util.OtpUtil;
import vn.edu.hcmute.util.PasswordUtil;

public class UserServiceImpl implements IUserService {

    /** Kiem tra dinh dang email o muc co ban (du de bat loi go sai). */
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final IUserDao userDao = new UserDao();

    @Override
    public User findById(int userid) {
        return userDao.findById(userid);
    }

    @Override
    public User findByEmail(String email) {
        return email == null ? null : userDao.findByEmail(email.trim());
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public int count() {
        return userDao.count();
    }

    // ==================== DANG KY + KICH HOAT ====================

    @Override
    public boolean register(String email, String fullname, String rawPassword, String confirmPassword) {
        email = normalizeEmail(email);
        validateEmail(email);
        validatePassword(rawPassword, confirmPassword);

        User existing = userDao.findByEmail(email);
        if (existing != null && existing.getStatus() != User.STATUS_INACTIVE) {
            throw new IllegalArgumentException("Email \"" + email + "\" đã được đăng ký");
        }

        String otp = OtpUtil.generate();

        User user;
        if (existing != null) {
            // Da dang ky nhung chua kich hoat -> cho dang ky lai, cap nhat thong tin va OTP moi
            user = existing;
            user.setFullname(fullname);
            user.setPassword(PasswordUtil.hash(rawPassword));
        } else {
            user = new User();
            user.setEmail(email);
            user.setFullname(fullname);
            user.setPassword(PasswordUtil.hash(rawPassword));
            user.setRole(User.ROLE_USER);
            user.setStatus(User.STATUS_INACTIVE);
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(Constants.OTP_EXPIRE_MINUTES));

        if (existing != null) {
            userDao.update(user);
        } else {
            userDao.insert(user);
        }

        return MailService.sendOtp(email, fullname, otp, "kích hoạt tài khoản", Constants.OTP_EXPIRE_MINUTES);
    }

    @Override
    public void activate(String email, String otp) {
        User user = requireUser(email);

        if (user.getStatus() == User.STATUS_ACTIVE) {
            throw new IllegalStateException("Tài khoản đã được kích hoạt, bạn có thể đăng nhập");
        }
        if (user.getOtp() == null) {
            throw new IllegalStateException("Tài khoản chưa có mã xác nhận, vui lòng gửi lại mã");
        }
        if (!user.isOtpValid(otp)) {
            throw new IllegalArgumentException("Mã xác nhận không đúng hoặc đã hết hạn");
        }

        user.setStatus(User.STATUS_ACTIVE);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userDao.update(user);
    }

    @Override
    public boolean resendActivationOtp(String email) {
        User user = requireUser(email);
        if (user.getStatus() == User.STATUS_ACTIVE) {
            throw new IllegalStateException("Tài khoản đã được kích hoạt");
        }
        String otp = OtpUtil.generate();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(Constants.OTP_EXPIRE_MINUTES));
        userDao.update(user);
        return MailService.sendOtp(user.getEmail(), user.getFullname(), otp,
                "kích hoạt tài khoản", Constants.OTP_EXPIRE_MINUTES);
    }

    // ==================== DANG NHAP ====================

    @Override
    public User login(String email, String rawPassword) {
        email = normalizeEmail(email);
        if (email.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập email và mật khẩu");
        }

        User user = userDao.findByEmail(email);

        // Thong bao chung cho ca 2 truong hop sai email / sai mat khau
        // -> khong cho ke tan cong biet email nao da ton tai.
        if (user == null || !PasswordUtil.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }
        if (user.getStatus() == User.STATUS_INACTIVE) {
            throw new IllegalStateException("Tài khoản chưa được kích hoạt. Vui lòng nhập mã OTP đã gửi tới email.");
        }
        if (user.getStatus() == User.STATUS_BLOCKED) {
            throw new IllegalStateException("Tài khoản đã bị khóa");
        }
        return user;
    }

    // ==================== QUEN MAT KHAU ====================

    @Override
    public boolean requestPasswordReset(String email) {
        email = normalizeEmail(email);
        validateEmail(email);

        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email \"" + email + "\" chưa được đăng ký trong hệ thống");
        }

        String otp = OtpUtil.generate();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(Constants.OTP_EXPIRE_MINUTES));
        userDao.update(user);

        return MailService.sendOtp(user.getEmail(), user.getFullname(), otp,
                "đặt lại mật khẩu", Constants.OTP_EXPIRE_MINUTES);
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword, String confirmPassword) {
        User user = requireUser(email);
        validatePassword(newPassword, confirmPassword);

        if (user.getOtp() == null) {
            throw new IllegalStateException("Chưa có yêu cầu đặt lại mật khẩu, vui lòng thực hiện lại");
        }
        if (!user.isOtpValid(otp)) {
            throw new IllegalArgumentException("Mã xác nhận không đúng hoặc đã hết hạn");
        }

        user.setPassword(PasswordUtil.hash(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        // Neu tai khoan chua kich hoat ma da dat lai duoc mat khau thi coi nhu da xac thuc email
        if (user.getStatus() == User.STATUS_INACTIVE) {
            user.setStatus(User.STATUS_ACTIVE);
        }
        userDao.update(user);
    }

    @Override
    public void updateProfile(User user) {
        userDao.update(user);
    }

    // ==================== TIEN ICH ====================

    private User requireUser(String email) {
        User user = userDao.findByEmail(normalizeEmail(email));
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản với email này");
        }
        return user;
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static void validateEmail(String email) {
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (!EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Email không đúng định dạng");
        }
    }

    private static void validatePassword(String password, String confirm) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        if (!password.equals(confirm)) {
            throw new IllegalArgumentException("Mật khẩu nhập lại không khớp");
        }
    }
}
