package no.rosbach.jcoru.rest.facade;

import no.rosbach.jcoru.rest.CompilerResourceBase;
import no.rosbach.jcoru.rest.JavaSourceStringDto;
import no.rosbach.jcoru.rest.reports.CompilationReport;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(CompilerResource.COMPILER_PATH)
public class CompilerResource extends CompilerResourceBase {
  public static final String COMPILER_PATH = "/compile";

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public CompilationReport compilePost(List<JavaSourceStringDto> javaSources) {
    throwBadRequestIfSourcesAreInvalid(javaSources);
    compile(javaSources);

    return reportBuilder.buildReport();
  }

}
