import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test fixture with two failing and one passing test. At least one test expects a total of three (3) tests.
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

  @Ignore
  @Test
  public void ignored() {
    assertTrue(true);
  }
}
