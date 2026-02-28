package rbac;

@FunctionalInterface
public interface UserFilter {
    boolean test(User user);
    default UserFilter and(UserFilter other) {
        return user -> other.test(user) && test(user);
    }
    default UserFilter or(UserFilter other) {
        return user -> other.test(user) || test(user);
    }
}
