package rbac;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RoleManager implements Repository<Role> {
    private final ConcurrentMap<String, Role> rolesById = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Role> rolesByName = new ConcurrentHashMap<>();

    public Optional<Role> findByName(String name) {
        return Optional.ofNullable(rolesByName.get(name));
    }
    public List<Role> findByFilter(RoleFilter filter) {
        return rolesById.values().stream().filter(filter::test).toList();
    }
    public List<Role> findAll(RoleFilter filter, Comparator<Role> sorter) {
        return rolesById.values().stream().filter(filter::test).sorted(sorter).toList();
    }
    public boolean exists(String name) {
        return rolesByName.containsKey(name);
    }
    public void addPermissionToRole(String roleName, Permission permission) {
        rolesByName.computeIfPresent(roleName, (key, value) -> {
            value.addPermission(permission);
            return value;
        });
    }
    public void removePermissionFromRole(String roleName, Permission permission) {
        rolesByName.computeIfPresent(roleName, (key, value) -> {
            value.removePermission(permission);
            return value;
        });
    }
    public List<Role> findRolesWithPermission(String permissionName, String resource) {
        return rolesByName.values().stream().
                filter(RoleFilters.hasPermission(permissionName, resource)::test).toList();
    }

    @Override
    public void add(Role item) {
        if (item == null) throw new IllegalArgumentException("rbac.Role cannot be null");
        rolesByName.putIfAbsent(item.name, item);
        rolesById.putIfAbsent(item.id, item);
    }

    @Override
    public boolean remove(Role item) {
        if (item == null) return false;
        return rolesById.remove(item.id, item) && rolesByName.remove(item.name, item);
    }

    @Override
    public Optional<Role> findById(String id) {
        return Optional.ofNullable(rolesById.get(id));
    }

    @Override
    public List<Role> findAll() {
        return new ArrayList<>(rolesByName.values());
    }

    @Override
    public int count() {
        return rolesByName.size();
    }

    @Override
    public void clear() {
        rolesByName.clear();
        rolesById.clear();
    }
}
