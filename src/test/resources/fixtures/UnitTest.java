import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mapster on 16.03.15.
 */
public class UnitTest {

    @Test
    public void test() {
        assertTrue(true);
    }

    @Test
    public void failingTest() {
        assertEquals("true", "false");
    }
}
