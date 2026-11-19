package vn.edu.hcmute.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Gui email (OTP) bang Jakarta Mail.
 *
 * Cau hinh lay theo thu tu uu tien:
 *   1. System property  -Dmail.username=... (dat trong setenv.bat cua Tomcat)
 *   2. File src/main/resources/mail.properties
 *
 * Neu chua dien mail.username / mail.password -> che do DEV:
 * khong gui mail that ma in OTP ra console Tomcat (catalina.out) de test.
 */
public final class MailService {

    private static final Properties CFG = new Properties();
    private static boolean loaded = false;

    private MailService() {
    }

    private static synchronized void load() {
        if (loaded) {
            return;
        }
        try (InputStream in = MailService.class.getResourceAsStream("/mail.properties")) {
            if (in != null) {
                CFG.load(in);
            }
        } catch (IOException e) {
            System.err.println("[MAIL] Khong doc duoc mail.properties: " + e.getMessage());
        }
        // System property uu tien hon file
        override("mail.smtp.host");
        override("mail.smtp.port");
        override("mail.smtp.auth");
        override("mail.smtp.starttls.enable");
        override("mail.smtp.ssl.enable");
        override("mail.username");
        override("mail.password");
        override("mail.from");
        loaded = true;
    }

    private static void override(String key) {
        String v = System.getProperty(key);
        if (v != null && !v.isBlank()) {
            CFG.setProperty(key, v);
        }
    }

    private static String get(String key, String def) {
        load();
        String v = CFG.getProperty(key);
        return (v == null || v.isBlank()) ? def : v.trim();
    }

    public static boolean isConfigured() {
        return !get("mail.username", "").isEmpty() && !get("mail.password", "").isEmpty();
    }

    public static String getFromAddress() {
        return get("mail.from", get("mail.username", "no-reply@ltweb.local"));
    }

    /**
     * Gui ma OTP.
     *
     * @param to      email nguoi nhan
     * @param fullname ten nguoi nhan (co the null)
     * @param otp     ma 6 so
     * @param purpose "kích hoạt tài khoản" hoac "đặt lại mật khẩu"
     * @return true neu gui that qua SMTP, false neu dang o che do DEV (chi in console)
     */
    public static boolean sendOtp(String to, String fullname, String otp, String purpose, int expireMinutes) {
        String subject = "[LTWeb] Mã xác nhận " + purpose;
        String html = """
                <div style="font-family:Segoe UI,Arial,sans-serif;max-width:520px;margin:0 auto;
                            border:1px solid #e5e7eb;border-radius:12px;overflow:hidden">
                  <div style="background:#4f46e5;color:#fff;padding:18px 24px;font-size:17px;font-weight:600">
                    LTWeb - Xác nhận tài khoản
                  </div>
                  <div style="padding:24px;color:#1f2937;font-size:14px;line-height:1.6">
                    <p>Xin chào <b>%s</b>,</p>
                    <p>Mã xác nhận để <b>%s</b> của bạn là:</p>
                    <div style="font-size:32px;font-weight:700;letter-spacing:8px;color:#4f46e5;
                                background:#f5f3ff;border-radius:10px;padding:16px;text-align:center;margin:18px 0">
                      %s
                    </div>
                    <p>Mã này có hiệu lực trong <b>%d phút</b> và chỉ dùng được một lần.</p>
                    <p style="color:#6b7280;font-size:12.5px">Nếu bạn không yêu cầu thao tác này, hãy bỏ qua email.</p>
                  </div>
                </div>
                """.formatted(escape(fullname == null || fullname.isBlank() ? to : fullname), purpose, otp, expireMinutes);

        if (!isConfigured()) {
            logDev(to, otp, purpose);
            return false;
        }

        try {
            Session session = buildSession();
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(getFromAddress(), "LTWeb"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(html, "text/html; charset=UTF-8");
            Transport.send(msg);
            System.out.println("[MAIL] Da gui OTP toi " + to);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailException("Không gửi được email tới " + to + ": " + e.getMessage(), e);
        }
    }

    private static Session buildSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", get("mail.smtp.host", "smtp.gmail.com"));
        props.put("mail.smtp.port", get("mail.smtp.port", "587"));
        props.put("mail.smtp.auth", get("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", get("mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.ssl.enable", get("mail.smtp.ssl.enable", "false"));
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        final String user = get("mail.username", "");
        final String pass = get("mail.password", "");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
    }

    /** Che do DEV: in OTP ra console de test khi chua co SMTP. */
    private static void logDev(String to, String otp, String purpose) {
        String line = "=".repeat(62);
        System.out.println("\n" + line);
        System.out.println(" [MAIL - DEV MODE] Chua cau hinh SMTP (mail.properties)");
        System.out.println(" To      : " + to);
        System.out.println(" Muc dich: " + purpose);
        System.out.println(" OTP     : " + otp);
        System.out.println(line + "\n");
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Loi gui mail - controller bat va hien thi cho nguoi dung. */
    public static class MailException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public MailException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
