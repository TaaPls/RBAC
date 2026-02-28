package rbac;

public record Permission(String name, String resource, String description) {
    public Permission(String name, String resource, String description) {
        if (name.contains(" "))
            throw new IllegalArgumentException("name не должно содержать пробелов");
        if (description.isEmpty())
            throw new IllegalArgumentException("description не может быть пустым");
        this.name = name.toUpperCase();
        this.resource = resource.toLowerCase();
        this.description = description;
    }
    public String format() {
        return name+" on "+resource+": "+description;
    }
    public boolean matches(String namePattern, String resourcePattern) {
        return name.equalsIgnoreCase(namePattern) && resource.equalsIgnoreCase(resourcePattern);
    }
}
