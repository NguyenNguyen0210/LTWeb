package vn.edu.hcmute.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "UQ_users_Email", columnNames = "Email"))
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Hang so cho cot Role */
    public static final int ROLE_USER = 0;
    public static final int ROLE_ADMIN = 1;

    /** Hang so cho cot Status */
    public static final int STATUS_INACTIVE = 0; // chua kich hoat
    public static final int STATUS_ACTIVE = 1;   // dang hoat dong
    public static final int STATUS_BLOCKED = -1; // bi khoa

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private int userid;

    @Version
    @Column(name = "UserVersion", nullable = false, columnDefinition = "bigint default 0")
    private long rowVersion;

    @Column(name = "Email", columnDefinition = "nvarchar(100) not null")
    private String email;

    @Column(name = "Fullname", columnDefinition = "nvarchar(100) null")
    private String fullname;

    /**
     * Mat khau KHONG BAO GIO luu dang thuong - luu chuoi BCrypt (60 ky tu,
     * dang $2a$10$...). Khong the dao nguoc, chi kiem tra bang BCrypt.checkpw().
     */
    @Column(name = "Password", columnDefinition = "nvarchar(100) not null")
    @ToString.Exclude
    private String password;

    @Column(name = "Phone", columnDefinition = "nvarchar(20) null")
    private String phone;

    @Column(name = "Avatar", columnDefinition = "nvarchar(500) null")
    private String avatar;

    @Column(name = "Role")
    private int role;

    @Column(name = "Status")
    private int status;

    /** Ma OTP 6 so dang cho (kich hoat tai khoan hoac dat lai mat khau) */
    @Column(name = "Otp", columnDefinition = "nvarchar(10) null")
    @ToString.Exclude
    private String otp;

    @Column(name = "OtpExpiry")
    private LocalDateTime otpExpiry;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    /* 1 user - n video */
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Video> videos = new ArrayList<>();

    /** Ten hien thi: uu tien fullname, khong co thi lay phan truoc @ cua email */
    public String getDisplayName() {
        if (fullname != null && !fullname.isBlank()) {
            return fullname;
        }
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf('@'));
        }
        return email;
    }

    public boolean isAdmin() {
        return role == ROLE_ADMIN;
    }

    public boolean isActive() {
        return status == STATUS_ACTIVE;
    }

    /** OTP con hieu luc hay khong */
    public boolean isOtpValid(String input) {
        return otp != null && otpExpiry != null && otp.equals(input) && otpExpiry.isAfter(LocalDateTime.now());
    }
}
