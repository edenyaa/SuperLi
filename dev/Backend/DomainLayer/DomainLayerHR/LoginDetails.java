package Backend.DomainLayer.DomainLayerHR;
import java.util.Objects;

public class LoginDetails {
    private String username;
    private String password;

    public LoginDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginDetails that = (LoginDetails) o;
        return password == that.password && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    public String getId() {
        return username;
    }
}
