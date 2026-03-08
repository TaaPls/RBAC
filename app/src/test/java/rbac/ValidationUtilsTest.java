package rbac;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class ValidationUtilsTest {
    @Test
    void validDateTest() {
        assertTrue(ValidationUtils.isValidDate("2020-11-11"));
    }
    @Test
    void invalidDateTest() {
        assertFalse(ValidationUtils.isValidDate("2020-43-11"));
        assertFalse(ValidationUtils.isValidDate("2020-12"));
        assertFalse(ValidationUtils.isValidDate(""));
    }
    @Test
    void validEmailTest() {
        assertTrue(ValidationUtils.isValidEmail("aga@test.com"));
    }
    @Test
    void invalidEmailTest() {
        assertFalse(ValidationUtils.isValidEmail("agatest.com"));
        assertFalse(ValidationUtils.isValidEmail("aga@testcom"));
        assertFalse(ValidationUtils.isValidEmail("aga"));
        assertFalse(ValidationUtils.isValidEmail(""));
    }
    @Test
    void validUsernameTest() {
        assertTrue(ValidationUtils.isValidUsername("Aga123"));
    }
    @Test
    void invalidUsernameTest() {
        assertFalse(ValidationUtils.isValidUsername("Aga asd"));
        assertFalse(ValidationUtils.isValidUsername("Aga$!"));
        assertFalse(ValidationUtils.isValidUsername(""));
    }
    @Test
    void requireNonEmptyTest() {
        assertDoesNotThrow(() -> ValidationUtils.requireNonEmpty("Nonempty"));
    }
    @Test
    void requireNonEmptyThrowsTest() {
        assertThrows(NullPointerException.class, () -> ValidationUtils.requireNonEmpty(""));
    }
    @Test
    void normalizeStringTest() {
        assertEquals("Full",ValidationUtils.normalizeString("   full   "));
    }
}