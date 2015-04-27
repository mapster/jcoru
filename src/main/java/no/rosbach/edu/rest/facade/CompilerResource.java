package no.rosbach.edu.rest.facade;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import no.rosbach.edu.rest.CompilerResourceBase;
import no.rosbach.edu.rest.JavaSourceStringDTO;
import no.rosbach.edu.rest.reports.CompilationReport;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path(CompilerResource.COMPILER_PATH)
public class CompilerResource extends CompilerResourceBase {
    public static final String COMPILER_PATH = "/compile";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CompilationReport compilePost(List<JavaSourceStringDTO> javaSources) {
        throwBadRequestIfSourcesAreInvalid(javaSources);
        compile(javaSources);

        return reportBuilder.buildReport();
    }

}
