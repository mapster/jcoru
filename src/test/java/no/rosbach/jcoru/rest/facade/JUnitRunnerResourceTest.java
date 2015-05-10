package no.rosbach.jcoru.rest.facade;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSources;
import static no.rosbach.jcoru.utils.Stream.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.filemanager.JavaSourceString;
import no.rosbach.jcoru.rest.JavaSourceStringDto;
import no.rosbach.jcoru.rest.reports.CompilationReport;
import no.rosbach.jcoru.rest.reports.JUnitReport;
import no.rosbach.jcoru.rest.reports.Report;

import org.junit.Test;

import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitRunnerResourceTest extends CompilerResourceTestBase {

  @Override
  protected Invocation.Builder request() {
    return target(JUnitRunnerResource.TEST_PATH).request();
  }

  @Override
  protected CompilationReport compilationReportFromResponse(Response response) {
    return response.readEntity(Report.class).compilationReport;
  }

  /**
   * Perform a request where JUnitReport is the expected response.
   *
   * @param javaSources the java sources to run tests on.
   * @return a report of the test execution.
   */
  protected JUnitReport junitReportRequest(List<JavaSourceString> javaSources) {
    List<JavaSourceStringDto> entity = stream(javaSources).map(JavaSourceStringDto::new).collect(toList());
    GenericEntity<List<JavaSourceStringDto>> listGenericEntity = new GenericEntity<List<JavaSourceStringDto>>(entity) {
    };

    Response response = request().post(Entity.entity(listGenericEntity, MediaType.APPLICATION_JSON));
    ResponseHandler.throwExceptionIfError(response);

    return response.readEntity(Report.class).junitReport;
  }

  @Test
  public void shouldReturnTestReport() {
    JUnitReport jUnitReport = junitReportRequest(getFixtureSources(Fixtures.FAIL_TEST));
    assertEquals(3, jUnitReport.getTests());
  }

  @Test
  public void shouldReturnTestReportWithFailuresList() {
    JUnitReport jUnitReport = junitReportRequest(getFixtureSources(Fixtures.FAIL_TEST));
    assertFalse(jUnitReport.getFailures().isEmpty());
  }

  @Test
  public void shouldOnlyAttemptToRunTestsInTestClasses() {
    JUnitReport report = junitReportRequest(getFixtureSources(Fixtures.TEST_SUBJECT, Fixtures.TEST_SUBJECT_TEST));
    assertEquals(0, report.getFailedTests());
  }

  @Test
  public void shouldRunTestsInAllTestClasses() {
    JUnitReport report = junitReportRequest(getFixtureSources(Fixtures.FAIL_TEST, Fixtures.SUCCESS_TEST));
    assertEquals(5, report.getTests());
  }

  @Test(expected = BadRequestException.class)
  public void shouldFailIfClassWithoutTestsIsNamedAsTestClass() {
    junitReportRequest(getFixtureSources(Fixtures.NOT_REALLY_TEST));
  }

  @Test
  public void shouldNotRunTestsForClassNotNamedAsTestClass() {
    JUnitReport report = junitReportRequest(getFixtureSources(Fixtures.NOT_NAMED_AS_TEST_CLASS));
    assertEquals(0, report.getTests());
  }
}
