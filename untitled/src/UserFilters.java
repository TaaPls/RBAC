import java.util.Objects;

public class UserFilters {
    public static UserFilter byUsername(String username) {
        return user -> Objects.equals(user.username(), username);
    }
    public static UserFilter byUsernameContains(String substring) {
        return user -> user.username().contains(substring);
    }
    public static UserFilter byEmail(String email) {
        return user -> Objects.equals(user.email(), email);
    }
    public static UserFilter byEmailDomain(String domain) {
        return user -> user.email().endsWith(domain);
    }
    public static UserFilter byFullNameContains(String substring) {
        return user -> user.fullName().contains(substring);
    }
}
