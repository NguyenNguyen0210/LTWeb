package vn.edu.hcmute.service;

import java.time.LocalDateTime;
import java.util.List;

import vn.edu.hcmute.dao.IUserDao;
import vn.edu.hcmute.dao.UserDao;
import vn.edu.hcmute.entity.User;
import vn.edu.hcmute.util.Constants;
import vn.edu.hcmute.util.FormValidation;
import vn.edu.hcmute.util.OtpUtil;
import vn.edu.hcmute.util.PasswordUtil;

public class UserServiceImpl implements IUserService {

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
        email = FormValidation.email(email);
        fullname = FormValidation.required(fullname, "Họ tên", 100);
        FormValidation.newPassword(rawPassword, confirmPassword);

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
        if (!user.isOtpValid(FormValidation.otp(otp))) {
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
        email = FormValidation.email(email);
        FormValidation.loginPassword(rawPassword);

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
        email = FormValidation.email(email);

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
        FormValidation.newPassword(newPassword, confirmPassword);

        if (user.getOtp() == null) {
            throw new IllegalStateException("Chưa có yêu cầu đặt lại mật khẩu, vui lòng thực hiện lại");
        }
        if (!user.isOtpValid(FormValidation.otp(otp))) {
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
        User user = userDao.findByEmail(FormValidation.email(email));
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản với email này");
        }
        return user;
    }
}
