package vn.edu.hcmute.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.Part;
import vn.edu.hcmute.util.FormValidation;
import vn.edu.hcmute.util.UploadUtil;

final class CloudinaryAvatarStore {

    private static final Set<String> FORMATS = Set.of("jpeg", "png", "gif");
    private static final long MAX_PIXELS = 20_000_000L;

    record Avatar(String url, String publicId) {
    }

    Avatar upload(Part part) throws IOException {
        if (part == null || part.getSubmittedFileName() == null || part.getSubmittedFileName().isBlank()) {
            return null;
        }
        if (part.getSize() == 0 || part.getSize() > UploadUtil.IMAGE_MAX_BYTES) {
            throw new IllegalArgumentException("Ảnh đại diện phải có dung lượng từ 1 byte đến 5 MB");
        }

        String format = validateImage(part);
        Cloudinary cloudinary = client();
        String publicId = "ltweb-bt3/avatars/" + UUID.randomUUID();
        try (InputStream image = part.getInputStream()) {
            Map<?, ?> response = cloudinary.uploader().upload(image.readAllBytes(), ObjectUtils.asMap(
                    "resource_type", "image", "allowed_formats", new String[] {"jpg", "jpeg", "png", "gif"},
                    "public_id", publicId, "filename", "avatar." + format, "overwrite", false));
            if (response.get("secure_url") instanceof String url && url.startsWith("https://")) {
                return new Avatar(FormValidation.mediaLink(url, null, "Link ảnh đại diện"), publicId);
            }
            throw new IOException("Cloudinary did not return a secure URL");
        } catch (IOException | RuntimeException e) {
            discard(cloudinary, publicId);
            throw new IOException("Không tải được ảnh đại diện lên Cloudinary", e);
        }
    }

    void discard(Avatar avatar) {
        if (avatar != null) {
            discard(client(), avatar.publicId());
        }
    }

    private void discard(Cloudinary cloudinary, String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException | RuntimeException e) {
            System.getLogger(getClass().getName()).log(System.Logger.Level.WARNING,
                    "Không dọn được ảnh Cloudinary: " + publicId);
        }
    }

    private static Cloudinary client() {
        String config = System.getenv("CLOUDINARY_URL");
        if (config == null || config.isBlank()) {
            throw new IllegalStateException("Chưa cấu hình CLOUDINARY_URL để tải ảnh đại diện");
        }
        try {
            return new Cloudinary(config);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("CLOUDINARY_URL không đúng định dạng");
        }
    }

    private String validateImage(Part part) {
        try (InputStream input = part.getInputStream(); ImageInputStream image = new MemoryCacheImageInputStream(input)) {
            var readers = ImageIO.getImageReaders(image);
            if (!readers.hasNext()) {
                throw invalidImage();
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(image, true, true);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!FORMATS.contains(format)) {
                    throw invalidImage();
                }
                if ((long) reader.getWidth(0) * reader.getHeight(0) > MAX_PIXELS) {
                    throw new IllegalArgumentException("Ảnh đại diện không được vượt quá 20 megapixel");
                }
                return format;
            } finally {
                reader.dispose();
            }
        } catch (IOException e) {
            throw invalidImage();
        }
    }

    private static IllegalArgumentException invalidImage() {
        return new IllegalArgumentException("Ảnh đại diện phải là ảnh JPEG, PNG hoặc GIF hợp lệ");
    }
}
