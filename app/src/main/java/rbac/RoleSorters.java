package rbac;

import java.util.Comparator;

public class RoleSorters {
    public static Comparator<Role> byName() {
        return (o1, o2) -> o1.name.compareToIgnoreCase(o2.name);
    }
    public static Comparator<Role> byPermissionCount() {
        return Comparator.comparingInt(o -> o.getPermissions().size());
    }
}
