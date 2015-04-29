package no.rosbach.jcoru.compile;

import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSource;
import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSources;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.filemanager.JavaSourceString;
import no.rosbach.jcoru.rest.reports.Report;

import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by mapster on 06.04.15.
 */
public class JUnitTestRunnerTest extends JUnitRunnerTestBase {

  @Test
  public void testAcceptsSuccessTestClassesAndReturnsResult() throws IOException, ClassNotFoundException {
    Report report = runTests(getFixtureSource(Fixtures.SUCCESS_TEST));
    assertTrue(report.junitReport.getFailures().isEmpty());
  }

  @Test
  public void testAcceptsFailTestClassesAndReturnsResult() {
    Report report = runTests(getFixtureSource(Fixtures.FAIL_TEST));
    assertFalse(report.junitReport.getFailures().isEmpty());
  }

  @Test
  public void testAcceptsListOfTestClassesAndReturnsResult() {
    Report report = runTests(getFixtureSources(Fixtures.FAIL_TEST, Fixtures.SUCCESS_TEST));
    assertFalse(report.junitReport.getFailures().isEmpty());
  }

  @Test
  public void testAcceptsEmptyList() {
    Report report = runTests(new LinkedList<JavaSourceString>());
    assertEquals(0, report.junitReport.getTests());
  }

  @Test
  public void testAcceptsMixOfTestAndNonTestClasses() {
    Report report = runTests(getFixtureSources(Fixtures.TEST_SUBJECT, Fixtures.TEST_SUBJECT_TEST));
    assertEquals(1, report.junitReport.getTests());
    assertTrue(report.junitReport.getFailures().isEmpty());
  }

}
