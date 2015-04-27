import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test fixture with two failing and one passing test. At least one test expects a total of three
 * (3) tests.
 */
public class FailTest {

    @Test
    public void test() {
        assertTrue(true);
    }

    @Test
    public void failingTest() {
        assertEquals("true", "false");
    }

    @Test
    public void failingTest2() {
        assertTrue(false);
    }
}
