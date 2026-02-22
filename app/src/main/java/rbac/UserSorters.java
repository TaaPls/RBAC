package rbac;

import java.util.Comparator;

public class UserSorters {
    public static Comparator<User> byUsername() {
        return (o1, o2) -> o1.username().compareToIgnoreCase(o2.username());
    }
    public static Comparator<User> byFullName() {
        return (o1, o2) -> o1.fullName().compareToIgnoreCase(o2.fullName());
    }
    public static Comparator<User> byEmail() {
        return (o1, o2) -> o1.email().compareToIgnoreCase(o2.email());
    }
}
