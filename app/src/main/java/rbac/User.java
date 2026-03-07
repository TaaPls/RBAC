package rbac;

import java.util.regex.Pattern;

public record User(String username, String fullName, String email) {
    public static User validate(String username, String fullName, String email) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException(
                    "Username must have 3 to 20 symbols, current length: " + username.length()
            );
        }
        if (!Pattern.compile("^\\w+$").matcher(username).matches()) {
            throw new IllegalArgumentException(
                    "Username can only consist of latin, numbers and underscore: " + username
            );
        }
        if (fullName == null) {
            throw new IllegalArgumentException("fullName cannot be null");
        }
        if (fullName.isEmpty()) {
            throw new IllegalArgumentException("fullName cannot be empty");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!Pattern.compile("^\\w+@\\w+\\.\\w+$").matcher(email).matches()) {
            throw new IllegalArgumentException(
                    "Email must contain '@' and '.': " + email
            );
        }
        return new User(username, fullName, email);
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
