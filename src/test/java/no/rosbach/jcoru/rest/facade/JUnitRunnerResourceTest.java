package no.rosbach.jcoru.rest.facade;

import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.filemanager.JavaSourceString;
import no.rosbach.jcoru.rest.JavaSourceStringDto;
import no.rosbach.jcoru.rest.reports.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSources;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JUnitRunnerResourceTest extends CompilerResourceTestBase {

  @Resource
  private JUnitRunnerResource jUnitRunnerResource;

  @Test
  public void shouldReturnTestReport() {
    JUnitReport jUnitReport = runTests(getFixtureSources(Fixtures.FAIL_TEST));
    assertEquals(3, jUnitReport.getTests());
  }

  @Test
  public void shouldReturnTestReportWithFailuresList() {
    JUnitReport jUnitReport = runTests(getFixtureSources(Fixtures.FAIL_TEST));
    assertFalse(jUnitReport.getFailures().isEmpty());
  }

  @Test
  public void shouldOnlyAttemptToRunTestsInTestClasses() {
    JUnitReport report = runTests(getFixtureSources(Fixtures.TEST_SUBJECT, Fixtures.TEST_SUBJECT_TEST));
    assertEquals(0, report.getFailedTests());
  }

  @Test
  public void shouldRunTestsInAllTestClasses() {
    JUnitReport report = runTests(getFixtureSources(Fixtures.FAIL_TEST, Fixtures.SUCCESS_TEST));
    assertEquals(5, report.getTests());
  }

  @Test(expected = BadRequestException.class)
  public void shouldFailIfClassWithoutTestsIsNamedAsTestClass() {
    runTests(getFixtureSources(Fixtures.NOT_REALLY_TEST));
  }

  @Test
  public void shouldNotRunTestsForClassNotNamedAsTestClass() {
    JUnitReport report = runTests(getFixtureSources(Fixtures.NOT_NAMED_AS_TEST_CLASS));
    assertEquals(0, report.getTests());
  }

  private JUnitReport runTests(List<JavaSourceString> sources) {
    return jUnitRunnerResource.runTests(sources.stream().map(JavaSourceStringDto::new).collect(toList())).junitReport;
  }

  @Override
  protected CompilationReport compile(List<JavaSourceStringDto> sources) {
    return jUnitRunnerResource.runTests(sources).compilationReport;
  }
}
