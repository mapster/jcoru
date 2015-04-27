package no.rosbach.edu.rest.facade;

import static java.util.stream.Collectors.toList;
import static no.rosbach.edu.utils.Stream.stream;

import no.rosbach.edu.compile.JUnitTestRunner;
import no.rosbach.edu.rest.CompilerResourceBase;
import no.rosbach.edu.rest.JavaSourceStringDto;
import no.rosbach.edu.rest.reports.CompilationReport;
import no.rosbach.edu.rest.reports.JUnitReport;
import no.rosbach.edu.rest.reports.Report;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.List;
import java.util.stream.Stream;

import javax.tools.JavaFileObject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(JUnitRunnerResource.TEST_PATH)
public class JUnitRunnerResource extends CompilerResourceBase {
  public static final String TEST_PATH = "/test";
  public static final String INITIALIZATION_ERROR_FAILURE = "initializationError";

  private final JUnitTestRunner testRunner = new JUnitTestRunner();

  public static void throwExceptionIfInitializationError(Failure failure) {
    if (failure.getDescription().getMethodName().equals(INITIALIZATION_ERROR_FAILURE)) {
      throw new BadRequestException(failure.getDescription().getDisplayName() + ": " + failure.getException().getMessage(), failure.getException());
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Report runTests(List<JavaSourceStringDto> javaSources) {
    throwBadRequestIfSourcesAreInvalid(javaSources);
    Iterable<? extends JavaFileObject> compiledClasses = compile(javaSources);

    // If compilation failed the return compilation report
    CompilationReport compilationReport = reportBuilder.buildReport();
    if (!compilationReport.isSuccess()) {
      return new Report(compilationReport);
    }

    Stream<? extends Class<?>> loadedClasses = stream(compiledClasses).map(c -> loadClass(c));
    Result testResult = testRunner.test(loadedClasses.filter(c -> c.getSimpleName().endsWith("Test")).collect(toList()));

    testResult.getFailures().stream().forEach(JUnitRunnerResource::throwExceptionIfInitializationError);

    return new Report(new JUnitReport(testResult));
  }
}
