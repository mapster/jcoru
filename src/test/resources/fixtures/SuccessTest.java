import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test fixture with two passing tests.
 */
public class SuccessTest {
    @Test
    public void test1() {
        assertEquals(1, 1);
    }

    @Test
    public void test2() {
        assertTrue(true);
    }
}
