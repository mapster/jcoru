package no.rosbach.jcoru.rest.facade;

import static java.lang.String.format;

import no.rosbach.jcoru.compile.JUnitTestRunner;
import no.rosbach.jcoru.compile.JavaCompileUtil;
import no.rosbach.jcoru.compile.NonRecoverableError;
import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.provider.JavaCompilerProvider;
import no.rosbach.jcoru.rest.CompilerResourceBase;
import no.rosbach.jcoru.rest.JavaSourceStringDto;
import no.rosbach.jcoru.rest.reports.CompilationReport;
import no.rosbach.jcoru.rest.reports.JUnitReport;
import no.rosbach.jcoru.rest.reports.JUnitReportFailure;
import no.rosbach.jcoru.rest.reports.Report;
import no.rosbach.jcoru.security.SandboxThread;

import java.util.List;

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

  public JUnitRunnerResource() {
    super(new JavaCompilerProvider().getJavaCompileUtil());
  }

  public JUnitRunnerResource(JavaCompileUtil compiler) {
    super(compiler);
  }

  private static void throwExceptionIfInitializationError(JUnitReportFailure failure) {
    if (failure.getTestMethodName().equals(INITIALIZATION_ERROR_FAILURE)) {
      Throwable exception = failure.getException();
      throw new BadRequestException(format("%s(%s): %s", failure.getTestMethodName(), failure.getTestClassName(), exception.getMessage()), exception);
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
  public Report runTests(List<JavaSourceStringDto> javaSources) {
    throwBadRequestIfSourcesAreInvalid(javaSources);

    List<CompiledClassObject> compiledClasses = compile(javaSources);
    // If compilation failed the return compilation report
    CompilationReport compilationReport = reportBuilder.buildReport();
    if (!compilationReport.isSuccess()) {
      return new Report(compilationReport);
    }

    final JUnitTestRunner testRunner = JUnitTestRunner.getRunner(compiledClasses, getClassLoader());
    SandboxThread sandboxThread = new SandboxThread(testRunner);
    sandboxThread.start();
    try {
      sandboxThread.join();
    } catch (InterruptedException e) {
      throw new NonRecoverableError("Sandbox was interrupted.", e);
    }

    final JUnitReport junitReport = new JUnitReport(testRunner.getResult());

    // Verify that tests ran successfully.
    if (junitReport != null) {
      junitReport.getFailures().stream().forEach(JUnitRunnerResource::throwExceptionIfInitializationError);
    }

    return new Report(junitReport);
  }

}
