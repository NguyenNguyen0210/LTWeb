package vn.edu.hcmute.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;

/**
 * Luu file upload bang @MultipartConfig (Part.write) - theo huong dan
 * "Upload File trong Java Servlet".
 */
public final class UploadUtil {

    public static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");
    public static final Set<String> VIDEO_EXT = Set.of("mp4", "webm", "ogg", "ogv", "mov", "m4v");

    private UploadUtil() {
    }

    /**
     * Luu 1 Part vao Constants.DIR.
     *
     * @return ten file moi, hoac null neu nguoi dung khong chon file
     * @throws IllegalArgumentException file sai dinh dang
     */
    public static String save(Part part, Set<String> allowed, String label) throws IOException, ServletException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        String submitted = part.getSubmittedFileName();
        if (submitted == null || submitted.isBlank()) {
            return null;
        }

        String filename = Paths.get(submitted).getFileName().toString();
        int dot = filename.lastIndexOf('.');
        String ext = dot >= 0 ? filename.substring(dot + 1).toLowerCase(Locale.ROOT) : "";

        if (!allowed.contains(ext)) {
            throw new IllegalArgumentException(label + " chỉ chấp nhận: " + String.join(", ", new TreeSet<>(allowed)));
        }

        File dir = new File(Constants.DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // timestamp + 3 so ngau nhien: duy nhat ke ca khi upload 2 file cung 1 giay
        String fname = System.currentTimeMillis() + "" + (100 + (int) (Math.random() * 900)) + "." + ext;
        part.write(Constants.DIR + File.separator + fname);
        return fname;
    }

    /** Xoa file upload cu (bo qua loi de khong lam hong thao tac update). */
    public static void deleteQuietly(String fname) {
        if (fname == null || fname.isBlank() || fname.startsWith("http")) {
            return;
        }
        try {
            new File(Constants.DIR, fname).delete();
        } catch (RuntimeException ignored) {
            // khong xoa duoc cung khong sao
        }
    }
}
