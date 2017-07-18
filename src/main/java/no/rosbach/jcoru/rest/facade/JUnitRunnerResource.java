package no.rosbach.jcoru.rest.facade;

import no.rosbach.jcoru.compile.JUnitTestRunner;
import no.rosbach.jcoru.compile.NonRecoverableError;
import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.rest.CompilerResourceBase;
import no.rosbach.jcoru.rest.JavaSourceStringDto;
import no.rosbach.jcoru.rest.reports.*;
import no.rosbach.jcoru.security.SandboxThread;
import no.rosbach.jcoru.security.StrictSecurityManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping(JUnitRunnerResource.TEST_PATH)
public class JUnitRunnerResource extends CompilerResourceBase {
  static final String TEST_PATH = "/test";

  @Resource
  private StrictSecurityManager strictSecurityManager;

  private static final String INITIALIZATION_ERROR_FAILURE = "initializationError";

  private static void throwExceptionIfInitializationError(JUnitReportFailure failure) {
    if (failure.getTestMethodName().equals(INITIALIZATION_ERROR_FAILURE)) {
      Throwable exception = failure.getException();
      throw new BadRequestException(format("%s(%s): %s", failure.getTestMethodName(), failure.getTestClassName(), exception.getMessage()), exception);
    }
  }

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Report runTests(@RequestBody List<JavaSourceStringDto> javaSources) {
    throwBadRequestIfSourcesAreInvalid(javaSources);

    List<CompiledClassObject> compiledClasses = compile(javaSources);
    // If compilation failed the return compilation report
    CompilationReport compilationReport = reportBuilder.buildReport();
    if (!compilationReport.isSuccess()) {
      return new Report(compilationReport);
    }

    final JUnitTestRunner testRunner = JUnitTestRunner.getRunner(compiledClasses, getClassLoader());
    SandboxThread sandboxThread = new SandboxThread(strictSecurityManager, testRunner);
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
