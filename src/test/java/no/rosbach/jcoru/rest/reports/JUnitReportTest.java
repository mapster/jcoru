package no.rosbach.jcoru.rest.reports;

import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import no.rosbach.jcoru.compile.JUnitRunnerTestBase;
import no.rosbach.jcoru.compile.fixtures.Fixtures;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitReportTest extends JUnitRunnerTestBase {

  public static final int EXPECTED_FAILURES = 2;
  public static final int EXPECTED_IGNORED = 1;
  public static final int EXPECTED_TESTS = 3;
  private JUnitReport jUnitReport;

  @Before
  public void prepare() {
    jUnitReport = runTests(getFixtureSource(Fixtures.FAIL_TEST)).junitReport;
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
