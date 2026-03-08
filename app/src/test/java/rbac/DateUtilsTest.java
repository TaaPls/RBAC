package rbac;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void isBeforeTest() {
        assertTrue(DateUtils.isBefore("2020-11-11", "2022-11-11"));
        assertFalse(DateUtils.isBefore("2022-11-11", "2020-11-11"));
    }

    @Test
    void isAfter() {
        assertTrue(DateUtils.isAfter("2022-11-11", "2020-11-11"));
        assertFalse(DateUtils.isAfter("2020-11-11", "2022-11-11"));
    }

    @Test
    void addDays() {
        assertEquals("2020-01-05", DateUtils.addDays("2020-01-01", 4));
        assertEquals("2020-02-01", DateUtils.addDays("2020-01-01", 31));
        assertEquals("2021-01-01", DateUtils.addDays("2020-12-01", 31));
    }

    @Test
    void formatRelativeTime() {
        assertEquals("March 09, 2024", DateUtils.formatRelativeTime("2024-03-09"));
    }
}