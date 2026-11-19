package vn.edu.hcmute.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "Videos")
@NamedQuery(name = "Video.findAll", query = "SELECT v FROM Video v")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "VideoId")
    private String videoId;

    @Column(name = "Active")
    private int active;

    @Column(name = "Description", columnDefinition = "nvarchar(500) null")
    private String description;

    @Column(name = "Poster", columnDefinition = "nvarchar(500) null")
    private String poster;

    @Column(name = "Title", columnDefinition = "nvarchar(500) null")
    private String title;

    @Column(name = "Views")
    private int views;

    /** Link video (YouTube / mp4 truc tiep) HOAC ten file da upload vao Constants.DIR */
    @Column(name = "VideoUrl", columnDefinition = "nvarchar(500) null")
    private String videoUrl;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    /* nhieu video thuoc 1 category (co the null) */
    @ManyToOne
    @JoinColumn(name = "CategoryId")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    /* nhieu video thuoc 1 user - quan he 1-n theo yeu cau */
    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    /** Ngay tao dang chuoi de hien thi tren JSP. */
    public String getCreatedAtText() {
        return createdAt == null ? "" : createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    // ==================== TIEN ICH CHO TRANG PLAY ====================

    /** videoUrl la link YouTube (youtube.com/watch, youtu.be, /embed). */
    public boolean isYoutube() {
        return youtubeId() != null;
    }

    /** Link nhung iframe cua YouTube, null neu khong phai YouTube. */
    public String getYoutubeEmbedUrl() {
        String id = youtubeId();
        return id == null ? null : "https://www.youtube.com/embed/" + id;
    }

    /** Link http(s) truc tiep toi file video (vd: https://.../abc.mp4). */
    public boolean isExternal() {
        return videoUrl != null && videoUrl.startsWith("http") && !isYoutube();
    }

    /** Ten file da upload len server (nam trong Constants.DIR). */
    public boolean isUploaded() {
        return videoUrl != null && !videoUrl.isBlank() && !videoUrl.startsWith("http");
    }

    /** Link poster la URL online (khac voi file upload). */
    public boolean isPosterUrl() {
        return poster != null && poster.startsWith("http");
    }

    private String youtubeId() {
        if (videoUrl == null) {
            return null;
        }
        String u = videoUrl.trim();
        if (u.contains("youtu.be/")) {
            return cutAfter(u, "youtu.be/");
        }
        if (u.contains("youtube.com/watch")) {
            int i = u.indexOf("v=");
            return i < 0 ? null : cutAfter(u.substring(i + 2), "");
        }
        if (u.contains("youtube.com/embed/")) {
            return cutAfter(u, "youtube.com/embed/");
        }
        return null;
    }

    private static String cutAfter(String s, String marker) {
        String rest = s.substring(s.indexOf(marker) + marker.length());
        int end = rest.length();
        for (char c : new char[] { '&', '?', '#', '/' }) {
            int i = rest.indexOf(c);
            if (i >= 0 && i < end) {
                end = i;
            }
        }
        String id = rest.substring(0, end).trim();
        return id.isEmpty() ? null : id;
    }
}
