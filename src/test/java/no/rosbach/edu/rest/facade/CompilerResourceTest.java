package no.rosbach.edu.rest.facade;

import static org.junit.Assert.assertTrue;

import no.rosbach.edu.rest.JavaSourceStringDto;
import no.rosbach.edu.rest.reports.CompilationReport;

import org.junit.Test;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

/**
 * Created by mapster on 05.04.15.
 */
public class CompilerResourceTest extends CompilerResourceTestBase {

  @Override
  protected Class[] getFacadesToTest() {
    return new Class[]{CompilerResource.class};
  }

  @Override
  protected Invocation.Builder request() {
    return target(CompilerResource.COMPILER_PATH).request();
  }

  @Override
  protected CompilationReport compilationReportFromResponse(Response response) {
    return response.readEntity(CompilationReport.class);
  }

  @Test
  public void compilerReturnsSuccessReport() {
    CompilationReport result = compilationReportRequest(TEST_CLASS_SOURCE, TEST_CLASS_I_SOURCE);
    assertTrue(result.isSuccess());
  }

  @Test
  public void returnsSuccessForEmptyList() {
    CompilationReport report = compilationReportRequest(new JavaSourceStringDto[0]);
    assertTrue(report.isSuccess());
  }
}
