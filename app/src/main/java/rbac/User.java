package rbac;

import java.util.Arrays;
import java.util.regex.Pattern;

public record User(String username, String fullName, String email) {
    public static User validate(String username, String fullName, String email) {
        if (!ValidationUtils.isValidUsername(username)) {
            throw new IllegalArgumentException("Username is not valid");
        }
        ValidationUtils.requireNonEmpty(fullName);
        String[] str = fullName.split(" ");
        String normalizedName = String.join(" ",
                Arrays.stream(str).map(ValidationUtils::normalizeString).toList());
        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("Email is not valid");
        }
        return new User(username, normalizedName, email);
    }
    public String format() {
        return username + " (" + fullName + ") "+"<"+email+">";
    }
    public static void main(String[] args) {
        try {
            User.validate("Aga", "", "aga@mail.ru");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        try {
            User.validate("Aga", "asdas", null);
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        try {
            User.validate("Aga&$", "Jofdgdfg", "aga@gmail.com");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        try {
            User.validate("Aga", "asddas", "agamail.ru");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(User.validate("Aga", "asddas", "aga@mail.ru").format());
    }
}
