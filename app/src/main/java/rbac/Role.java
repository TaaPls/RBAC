package rbac;

import java.util.*;

public class Role {
    String id;
    String name;
    String description;
    private final Set<Permission> permissions;

    public Role(String name) {
        this.id = "role_"+ UUID.randomUUID();
        this.name = name;
        permissions = new HashSet<>();
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }
    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
    public boolean hasPermission(String permissionName, String resource) {
        for (Permission permission : permissions) {
            if (permission.matches(permissionName, resource))
                return true;
        }
        return false;
    }
    String format() {
        StringBuilder permissionString = new StringBuilder();
        for (Permission permission : permissions)
            permissionString.append("- ").append(permission.format()).append("\n");
        return "rbac.Role: "+name+" [ID: "+id+"]\n"+"Description: "
                +description+"\nPermissions ("+permissions.size()
                +")\n"+permissionString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return format();
    }

    public Set<Permission> getPermissions() {
        return new HashSet<>(permissions);
    }
}
