package rbac;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AssignmentManager implements Repository<RoleAssignment>{
    ConcurrentMap<String, RoleAssignment> assignments = new ConcurrentHashMap<>();

    public synchronized List<RoleAssignment> findByUser(User user) {
        return assignments.values().stream().filter(AssignmentFilters.byUser(user)::test).toList();
    }
    public synchronized List<RoleAssignment> findByRole(Role role) {
        return assignments.values().stream().filter(AssignmentFilters.byRole(role)::test).toList();
    }
    public synchronized List<RoleAssignment> findByFilter(AssignmentFilter filter) {
        return assignments.values().stream().filter(filter::test).toList();
    }
    public synchronized List<RoleAssignment> findByFilterParallel(AssignmentFilter filter) {
        return assignments.values().parallelStream().filter(filter::test).toList();
    }
    public synchronized List<RoleAssignment> findAll(AssignmentFilter filter, Comparator<RoleAssignment> sorter) {
        return assignments.values().stream().filter(filter::test).sorted(sorter).toList();
    }
    public synchronized List<RoleAssignment> getActiveAssignments() {
        return assignments.values().stream().filter(AssignmentFilters.activeOnly()::test).toList();
    }
    public synchronized List<RoleAssignment> getExpiredAssignments() {
        return assignments.values().stream().
                filter(AssignmentFilters.expiringBefore(LocalDate.now().toString())::test).toList();
    }
    public synchronized boolean userHasRole(User user, Role role) {
        return assignments.values().stream().
                anyMatch(AssignmentFilters.byUser(user).and(AssignmentFilters.byRole(role))::test);
    }
    public synchronized boolean userHasPermission(User user, String permissionName, String resource) {
        return assignments.values().stream().
                filter(AssignmentFilters.byUser(user)::test).
                anyMatch(roleAssignment ->
                        roleAssignment.role().hasPermission(permissionName, resource));
    }
    public synchronized Set<Permission> getUserPermissions(User user) {
        return assignments.values().stream().
                filter(AssignmentFilters.byUser(user)::test).
                flatMap(roleAssignment -> roleAssignment.role().getPermissions().stream()).
                collect(Collectors.toSet());
    }
    public void revokeAssignment(String assignmentId) {
        assignments.computeIfPresent(assignmentId, (key, value) -> {
            if (value instanceof PermanentAssignment o) {
                o.Revoke();
            }
            return value;
        });
    }
    public void extendTemporaryAssignment(String assignmentId, String newExpirationDate) {
        assignments.computeIfPresent(assignmentId, (key, value) -> {
            if (value instanceof TemporaryAssignment o) {
                o.extend(newExpirationDate);
            }
            return value;
        });
    }

    @Override
    public void add(RoleAssignment item) {
        if (item == null) throw new IllegalArgumentException("Assignment cannot be null");
        if (userHasRole(item.user(), item.role())) return;
        assignments.putIfAbsent(item.assignmentId(), item);
    }

    @Override
    public boolean remove(RoleAssignment item) {
        if (item == null) return false;
        return assignments.remove(item.assignmentId(), item);
    }

    @Override
    public Optional<RoleAssignment> findById(String id) {
        return Optional.ofNullable(assignments.get(id));
    }

    @Override
    public synchronized List<RoleAssignment> findAll() {
        return new ArrayList<>(assignments.values());
    }

    @Override
    public int count() {
        return assignments.size();
    }

    @Override
    public void clear() {
        assignments.clear();
    }
}
