package vn.edu.hcmute.util;

import java.io.File;

/**
 * Hang so dung chung.
 *
 * DIR la thu muc vat ly de luu anh upload. Khong luu vao trong WAR vi moi lan
 * deploy lai thi WAR bi ghi de -> mat anh.
 * Co the doi bang cach truyen -Dltweb.upload.dir=... cho Tomcat.
 */
public final class Constants {

    public static final String DIR = System.getProperty(
            "ltweb.upload.dir",
            System.getProperty("user.home") + File.separator + "ltweb-uploads");

    /** Anh mac dinh khi nguoi dung khong nhap link va khong upload file. */
    public static final String DEFAULT_IMAGE = "avatar.png";

    /** So dong tren mot trang danh sach category. */
    public static final int PAGE_SIZE = 5;

    /** So video tren mot trang o trang /video (theo yeu cau: 6sp/trang). */
    public static final int VIDEO_PAGE_SIZE = 6;

    /** So video moi nhat hien thi o trang chu. */
    public static final int HOME_VIDEO_LIMIT = 10;

    /** OTP het han sau bao nhieu phut. */
    public static final int OTP_EXPIRE_MINUTES = 5;

    private Constants() {
    }
}
