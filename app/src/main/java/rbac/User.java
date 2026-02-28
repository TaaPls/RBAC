package rbac;

import java.util.regex.Pattern;

public record User(String username, String fullName, String email) {
    public static User validate(String username, String fullName, String email) {
        if (username == null) {
            throw new IllegalArgumentException("Username не может быть null");
        }
        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username не может быть пустой строкой");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException(
                    "Username должен быть от 3 до 20 символов, текущая длина: " + username.length()
            );
        }
        if (!Pattern.compile("^\\w+$").matcher(username).matches()) {
            throw new IllegalArgumentException(
                    "Username может содержать только латинские буквы, цифры и подчеркивание: " + username
            );
        }
        if (fullName == null) {
            throw new IllegalArgumentException("fullName не может быть null");
        }
        if (fullName.isEmpty()) {
            throw new IllegalArgumentException("fullName не может быть пустой строкой");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email не может быть null");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустой строкой");
        }
        if (!Pattern.compile("^\\w+@\\w+\\.\\w+$").matcher(email).matches()) {
            throw new IllegalArgumentException(
                    "Email должен содержать @ и точку: " + email
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
