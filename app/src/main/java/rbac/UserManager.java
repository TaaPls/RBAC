package rbac;

import java.util.*;

public class UserManager implements Repository<User>{
    private final Map<String, User> users = new HashMap<>();

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    };
    public Optional<User> findByEmail(String email) {
        return users.values().stream().
                filter(user -> Objects.equals(user.email(), email)).findAny();
    }
    public List<User> findByFilter(UserFilter filter) {
        return users.values().stream().filter(filter::test).toList();
    };
    public List<User> findAll(UserFilter filter, Comparator<User> sorter) {
        return users.values().stream().filter(filter::test).sorted(sorter).toList();
    };
    public boolean exists(String username) {
        return users.containsKey(username);
    };
    public void update(String username, String newFullName, String newEmail) {
        if (users.containsKey(username)) {
            users.replace(username, new User(username, newFullName, newEmail));
        }
    };

    @Override
    public void add(User item) {
        if (item == null) throw new IllegalArgumentException("rbac.User cannot be null");
        if (users.containsKey(item.username()))
            throw new IllegalArgumentException("Key "+item.username()+" already exists");
        users.put(item.username(), item);
    }

    @Override
    public boolean remove(User item) {
        if (item == null) return false;
        return users.remove(item.username(), item);
    }

    @Override
    public Optional<User> findById(String id) {
        throw new UnsupportedOperationException("Id not supported for users");
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int count() {
        return users.size();
    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserManager that = (UserManager) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(users);
    }
}
