package no.rosbach.jcoru.rest.reports;

import no.rosbach.jcoru.compile.JUnitRunnerTestBase;
import no.rosbach.jcoru.compile.fixtures.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JUnitReportTest extends JUnitRunnerTestBase {

  public static final int EXPECTED_FAILURES = 2;
  public static final int EXPECTED_IGNORED = 1;
  public static final int EXPECTED_TESTS = 3;
  private JUnitReport jUnitReport;

  @Before
  public void prepare() {
    jUnitReport = new JUnitReport(runTests(getFixtureSource(Fixtures.FAIL_TEST)));
  }

  @Test
  public void shouldCopyRunCount() {
    assertEquals(EXPECTED_TESTS, jUnitReport.getTests());
  }

  @Test
  public void shouldCopyFailureCount() {
    assertEquals(EXPECTED_FAILURES, jUnitReport.getFailedTests());
  }

  @Test
  public void shouldCopyRunTime() {
    assertTrue(jUnitReport.getRunTime() >= 0);
  }

  @Test
  public void shouldCopyIgnored() {
    assertEquals(EXPECTED_IGNORED, jUnitReport.getIgnored());
  }

  @Test
  public void shouldCreateFailuresFromResult() {
    assertEquals(EXPECTED_FAILURES, jUnitReport.getFailures().size());
  }
}
