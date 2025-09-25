package Backend.DTO;

public class LoginDetailsDTO {
    private String username;
    private String password;

    public LoginDetailsDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}