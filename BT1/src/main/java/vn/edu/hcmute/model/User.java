package vn.edu.hcmute.model;

/**
 * Tang MODEL - doi tuong du lieu nguoi dung.
 * Luu y: chi la vi du hoc tap, mat khau luu plaintext KHONG dung trong thuc te.
 */
public class User {

    private final String username;
    private final String password;
    private final String fullName;

    public User(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return "User{" + username + ", " + fullName + "}";
    }
}
