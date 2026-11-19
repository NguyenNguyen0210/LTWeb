package vn.edu.hcmute.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Bam mat khau bang BCrypt.
 *
 * Vi sao khong dung MD5/SHA-256?
 *  - MD5/SHA-256 rat nhanh -> may tinh co the thu hang ty mat khau moi giay (brute force).
 *  - BCrypt co "cost" (work factor): cost 10 nghia la 2^10 vong lap, co tinh lam cham
 *    (~100ms cho moi lan hash) nen thu mat khau tro nen rat dat.
 *  - BCrypt tu sinh salt ngau nhien va nhung salt vao chuoi hash, nen 2 nguoi cung
 *    mat khau se co 2 chuoi hash khac nhau (chan rainbow table).
 */
public final class PasswordUtil {

    /** 10 ~ 2^10 vong lap, can bang giua bao mat va toc do dang nhap. */
    private static final int COST = 10;

    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(COST));
    }

    /** So sanh mat khau nguoi dung nhap voi chuoi hash trong DB. */
    public static boolean matches(String rawPassword, String hashed) {
        if (rawPassword == null || hashed == null || hashed.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(rawPassword, hashed);
        } catch (IllegalArgumentException e) {
            // Chuoi hash khong dung dinh dang BCrypt (vi du du lieu cu luu plaintext)
            return false;
        }
    }
}
