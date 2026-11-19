package vn.edu.hcmute.util;

import java.security.SecureRandom;

/** Sinh ma OTP 6 chu so. */
public final class OtpUtil {

    // SecureRandom (khong phai Random) vi Random co the doan duoc trang thai tiep theo.
    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpUtil() {
    }

    public static String generate() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}
