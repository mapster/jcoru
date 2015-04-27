import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by mapster on 25.04.15.
 */
public class TestSubjectTest {

  @Test
  public void test() {
    TestSubject subject = new TestSubject();
    assertEquals(true, subject.returnTrue());
  }
}
