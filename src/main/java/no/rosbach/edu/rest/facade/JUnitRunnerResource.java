package no.rosbach.edu.rest.facade;

import no.rosbach.edu.compile.JUnitTestRunner;
import no.rosbach.edu.rest.CompilerResourceBase;
import no.rosbach.edu.rest.JavaSourceStringDTO;
import no.rosbach.edu.rest.reports.CompilationReport;
import no.rosbach.edu.rest.reports.JUnitReport;
import no.rosbach.edu.rest.reports.Report;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import javax.tools.JavaFileObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static no.rosbach.edu.utils.Stream.stream;

/**
 * Created by mapster on 26.04.15.
 */
@Path(JUnitRunnerResource.TEST_PATH)
public class JUnitRunnerResource extends CompilerResourceBase {
    public static final String TEST_PATH = "/test";
    public static final String INITIALIZATION_ERROR_FAILURE = "initializationError";

    private final JUnitTestRunner testRunner = new JUnitTestRunner();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Report runTests(List<JavaSourceStringDTO> javaSources) {
        throwBadRequestIfSourcesAreInvalid(javaSources);
        Iterable<? extends JavaFileObject> compiledClasses = compile(javaSources);

        // If compilation failed the return compilation report
        CompilationReport compilationReport = reportBuilder.buildReport();
        if(!compilationReport.isSuccess()) {
            return new Report(compilationReport);
        }

        Stream<? extends Class<?>> loadedClasses = stream(compiledClasses).map(c -> loadClass(c));
        Result testResult = testRunner.test(loadedClasses.filter(c -> c.getSimpleName().endsWith("Test")).collect(toList()));

        testResult.getFailures().stream().forEach(JUnitRunnerResource::throwExceptionIfInitializationError);

        return new Report(new JUnitReport(testResult));
    }

    public static void throwExceptionIfInitializationError(Failure failure) {
        if(failure.getDescription().getMethodName().equals(INITIALIZATION_ERROR_FAILURE)) {
            throw new BadRequestException(failure.getDescription().getDisplayName() + ": " + failure.getException().getMessage(), failure.getException());
        }
    }
}
