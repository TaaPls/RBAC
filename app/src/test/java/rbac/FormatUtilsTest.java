package rbac;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatUtilsTest {

    @Test
    void truncateTest() {
        assertEquals("test", FormatUtils.truncate("test", 10));
        assertEquals("longs...", FormatUtils.truncate("longstring", 8));
    }

    @Test
    void padRightTest() {
        assertEquals("text    ", FormatUtils.padRight("text", 8));
    }

    @Test
    void padLeft() {
        assertEquals("    text", FormatUtils.padLeft("text", 8));
    }
}