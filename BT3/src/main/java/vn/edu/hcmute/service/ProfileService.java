package vn.edu.hcmute.service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

import jakarta.servlet.http.Part;
import vn.edu.hcmute.dao.IUserDao;
import vn.edu.hcmute.dao.UserDao;
import vn.edu.hcmute.entity.User;
import vn.edu.hcmute.util.Constants;
import vn.edu.hcmute.util.FormValidation;
import vn.edu.hcmute.util.OtpUtil;
import vn.edu.hcmute.util.PasswordUtil;

public class ProfileService {

    private static final Pattern PHONE = Pattern.compile("\\+?[0-9(). -]{7,20}");

    private final IUserDao users = new UserDao();
    private final CloudinaryAvatarStore avatars = new CloudinaryAvatarStore();

    public User findActiveUser(int userId) {
        User user = users.findById(userId);
        return user != null && user.isActive() ? user : null;
    }

    public void updateDetails(User user, String fullname, String phone, Part photo) throws IOException {
        String name = FormValidation.required(fullname, "Họ tên", 100);
        String number = FormValidation.optional(phone, "Số điện thoại", 20);
        if (number != null && (!PHONE.matcher(number).matches()
                || number.chars().filter(Character::isDigit).count() < 7)) {
            throw new IllegalArgumentException("Số điện thoại phải có ít nhất 7 chữ số");
        }

        CloudinaryAvatarStore.Avatar avatar = avatars.upload(photo);
        try {
            if (!users.updateProfileDetails(user.getUserid(), name, number, avatar == null ? null : avatar.url())) {
                throw new IllegalStateException("Tài khoản hiện không thể cập nhật");
            }
        } catch (RuntimeException e) {
            avatars.discard(avatar);
            throw e;
        }
    }

    public EmailChange requestEmailChange(User user, String email, String password) {
        String newEmail = FormValidation.email(email);
        FormValidation.loginPassword(password);
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }
        if (newEmail.equals(FormValidation.normalizeEmail(user.getEmail()))) {
            throw new IllegalArgumentException("Hãy nhập email khác với email hiện tại");
        }
        requireAvailableEmail(newEmail);
        String otp = OtpUtil.generate();
        boolean delivered = MailService.sendOtp(newEmail, user.getFullname(), otp,
                "đổi email", Constants.OTP_EXPIRE_MINUTES);
        return new EmailChange(user.getEmail(), newEmail, otp,
                Instant.now().plus(Constants.OTP_EXPIRE_MINUTES, ChronoUnit.MINUTES), delivered);
    }

    public void confirmEmailChange(User user, EmailChange pending, String otp) {
        if (pending == null || !user.getEmail().equals(pending.currentEmail())) {
            throw new IllegalStateException("Hãy yêu cầu mã xác nhận đổi email trước");
        }
        pending.verify(otp, Instant.now());
        requireAvailableEmail(pending.getNewEmail());
        if (!users.changeEmail(user.getUserid(), pending.currentEmail(), pending.getNewEmail())) {
            throw new IllegalStateException("Email hiện tại đã thay đổi. Hãy tải lại trang");
        }
    }

    private void requireAvailableEmail(String email) {
        if (users.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email mới đã được sử dụng");
        }
    }
}
