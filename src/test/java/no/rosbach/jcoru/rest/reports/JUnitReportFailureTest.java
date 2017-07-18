package no.rosbach.jcoru.rest.reports;

import no.rosbach.jcoru.compile.JUnitRunnerTestBase;
import no.rosbach.jcoru.compile.fixtures.Fixtures;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static no.rosbach.jcoru.compile.fixtures.Fixtures.FAIL_TEST;
import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JUnitReportFailureTest extends JUnitRunnerTestBase {

  public static final String TEST_NAME = "failingTest";
  private JUnitReportFailure failure;

  @Before
  public void prepare() {
    failure = new JUnitReport(runTests(getFixtureSource(Fixtures.FAIL_TEST))).getFailures().stream()
        .filter(f -> f.getTestMethodName().equals(TEST_NAME)).findFirst()
        .orElseThrow(() -> new Error("Fixture contains no failing tests."));
    ;
  }

  @Test
  public void shouldCopyTestClassNameFromDescription() {
    assertEquals(FAIL_TEST.toString(), failure.getTestClassName());
  }

  @Test
  public void shouldCopyTestMethodNameFromDescription() {
    assertEquals(TEST_NAME, failure.getTestMethodName());
  }

  @Test
  public void shouldCopyFailureType() {
    assertEquals(ComparisonFailure.class.getSimpleName(), failure.getFailureType());
  }

  @Test
  public void shouldCopyExpectedForComparisonFailure() {
    assertEquals("true", failure.getExpected());
  }

  @Test
  public void shouldCopyActualForComparisonFailure() {
    assertEquals("false", failure.getActual());
  }
}
