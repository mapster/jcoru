package no.rosbach.edu.rest.facade;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import no.rosbach.edu.compile.JUnitTestRunner;
import no.rosbach.edu.rest.CompilerResourceBase;
import no.rosbach.edu.rest.JavaSourceStringDto;
import no.rosbach.edu.rest.reports.JUnitReportFailure;
import no.rosbach.edu.rest.reports.Report;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path(JUnitRunnerResource.TEST_PATH)
public class JUnitRunnerResource extends CompilerResourceBase {
  public static final String TEST_PATH = "/test";
  public static final String INITIALIZATION_ERROR_FAILURE = "initializationError";

  private static void throwExceptionIfInitializationError(JUnitReportFailure failure) {
    if (failure.getTestMethodName().equals(INITIALIZATION_ERROR_FAILURE)) {
      Throwable exception = failure.getException();
      throw new BadRequestException(format("%s(%s): %s", failure.getTestMethodName(), failure.getTestClassName(), exception.getMessage()), exception);
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Report runTests(List<JavaSourceStringDto> javaSources) {
    throwBadRequestIfSourcesAreInvalid(javaSources);

    final JUnitTestRunner testRunner = new JUnitTestRunner(javaSources.stream().map(JavaSourceStringDto::create).collect(toList()));
    testRunner.run();
    final Report report = testRunner.getReport();

    // Verify that tests ran successfully.
    if (report.junitReport != null) {
      report.junitReport.getFailures().stream().forEach(JUnitRunnerResource::throwExceptionIfInitializationError);
    }

    return report;
  }

  @GET
  public String testing(@QueryParam("f") String filename) throws IOException {
    System.exit(1);
    return IOUtils.toString(new FileInputStream(filename));
  }
}
