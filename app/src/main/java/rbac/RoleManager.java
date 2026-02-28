package rbac;

import java.util.*;

public class RoleManager implements Repository<Role> {
    private final Map<String, Role> rolesById = new HashMap<>();
    private final Map<String, Role> rolesByName = new HashMap<>();

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
        if (rolesByName.containsKey(roleName)) {
            rolesByName.get(roleName).addPermission(permission);
        }
    }
    public void removePermissionFromRole(String roleName, Permission permission) {
        if (rolesByName.containsKey(roleName)) {
            rolesByName.get(roleName).removePermission(permission);
        }
    }
    public List<Role> findRolesWithPermission(String permissionName, String resource) {
        return rolesByName.values().stream().
                filter(RoleFilters.hasPermission(permissionName, resource)::test).toList();
    }

    @Override
    public void add(Role item) {
        if (item == null) throw new IllegalArgumentException("rbac.Role cannot be null");
        if (rolesByName.containsValue(item)) throw new IllegalArgumentException("rbac.Role already exists");
        rolesByName.put(item.name, item);
        rolesById.put(item.id, item);
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
