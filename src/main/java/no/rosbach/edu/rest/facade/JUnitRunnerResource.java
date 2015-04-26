package no.rosbach.edu.rest.facade;

import no.rosbach.edu.rest.CompilerResourceBase;
import no.rosbach.edu.rest.JavaSourceStringDTO;
import no.rosbach.edu.rest.reports.CompilationReport;
import no.rosbach.edu.rest.reports.Report;

import javax.tools.JavaFileObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by mapster on 26.04.15.
 */
@Path(JUnitRunnerResource.TEST_PATH)
public class JUnitRunnerResource extends CompilerResourceBase {
    public static final String TEST_PATH = "/test";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Report runTests(List<JavaSourceStringDTO> javaSources) {
        throwBadRequestIfSourcesAreInvalid(javaSources);
        Iterable<? extends JavaFileObject> compile = compile(javaSources);

        // If compilation failed the return compilation report
        CompilationReport compilationReport = reportBuilder.buildReport();
        if(!compilationReport.isSuccess()) {
            return compilationReport;
        }

        return null;
    }
}
