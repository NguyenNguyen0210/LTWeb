package vn.edu.hcmute.util;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

public final class FormValidation {

    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9\\-]+(?:\\.[A-Za-z0-9\\-]+)+$");
    private static final Pattern OTP = Pattern.compile("[0-9]{6}");

    private FormValidation() {
    }

    public static String normalizeEmail(String value) {
        return value == null ? "" : value.strip().toLowerCase(Locale.ROOT);
    }

    public static String email(String value) {
        String email = normalizeEmail(value);
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (email.length() > 100 || !EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Email không đúng định dạng hoặc vượt quá 100 ký tự");
        }
        return email;
    }

    public static String required(String value, String label, int maxLength) {
        String text = optional(value, label, maxLength);
        if (text == null) {
            throw new IllegalArgumentException(label + " không được để trống");
        }
        return text;
    }

    public static String optional(String value, String label, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String text = value.strip();
        if (text.length() > maxLength) {
            throw new IllegalArgumentException(label + " không được vượt quá " + maxLength + " ký tự");
        }
        return text;
    }

    public static void loginPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (password.length() > 72 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Mật khẩu không được vượt quá 72 byte UTF-8");
        }
    }

    public static void newPassword(String password, String confirmation) {
        loginPassword(password);
        if (password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        if (!password.equals(confirmation)) {
            throw new IllegalArgumentException("Mật khẩu nhập lại không khớp");
        }
    }

    public static String otp(String value) {
        if (value == null || !OTP.matcher(value.strip()).matches()) {
            throw new IllegalArgumentException("Mã xác nhận phải gồm 6 chữ số");
        }
        return value.strip();
    }

    public static int state(String value, int fallback) {
        if (value == null) {
            return fallback;
        }
        return switch (value) {
            case "1" -> 1;
            case "0" -> 0;
            default -> throw new IllegalArgumentException("Trạng thái không hợp lệ");
        };
    }

    public static String mediaLink(String value, String currentValue, String label) {
        String link = optional(value, label, 500);
        if (link == null || link.equals(currentValue)) {
            return link;
        }
        try {
            URI uri = URI.create(link);
            if (("https".equals(uri.getScheme()) || "http".equals(uri.getScheme()))
                    && uri.getHost() != null && uri.getUserInfo() == null) {
                return link;
            }
        } catch (IllegalArgumentException ignored) {
            // The form uses the same message for malformed and unsupported URLs.
        }
        throw new IllegalArgumentException(label + " phải là URL HTTP hoặc HTTPS hợp lệ");
    }
}
