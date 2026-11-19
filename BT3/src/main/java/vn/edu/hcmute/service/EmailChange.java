package vn.edu.hcmute.service;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

import vn.edu.hcmute.util.FormValidation;

public final class EmailChange implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int MAX_ATTEMPTS = 5;

    private final String currentEmail;
    private final String newEmail;
    private final String otp;
    private final Instant expiresAt;
    private final boolean delivered;
    private int attemptsRemaining = MAX_ATTEMPTS;

    EmailChange(String currentEmail, String newEmail, String otp, Instant expiresAt, boolean delivered) {
        this.currentEmail = currentEmail;
        this.newEmail = newEmail;
        this.otp = otp;
        this.expiresAt = expiresAt;
        this.delivered = delivered;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public boolean isDelivered() {
        return delivered;
    }

    String currentEmail() {
        return currentEmail;
    }

    public boolean isUsable(Instant time) {
        return time.isBefore(expiresAt) && attemptsRemaining > 0;
    }

    void verify(String input, Instant time) {
        if (!isUsable(time)) {
            throw new IllegalStateException("Mã xác nhận đã hết hạn hoặc hết lượt thử. Hãy yêu cầu mã mới.");
        }
        String candidate = FormValidation.otp(input);
        if (!MessageDigest.isEqual(otp.getBytes(StandardCharsets.US_ASCII), candidate.getBytes(StandardCharsets.US_ASCII))) {
            attemptsRemaining--;
            throw new IllegalArgumentException(attemptsRemaining == 0
                    ? "Đã hết lượt thử. Hãy yêu cầu mã mới."
                    : "Mã xác nhận không đúng. Còn " + attemptsRemaining + " lượt thử.");
        }
    }
}
