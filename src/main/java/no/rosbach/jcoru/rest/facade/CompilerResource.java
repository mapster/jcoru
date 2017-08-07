package no.rosbach.jcoru.rest.facade;

import no.rosbach.jcoru.rest.CompilerResourceBase;
import no.rosbach.jcoru.rest.JavaSourceStringDto;
import no.rosbach.jcoru.rest.reports.CompilationReport;
import no.rosbach.jcoru.rest.reports.CompilationReportBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(CompilerResource.COMPILER_PATH)
public class CompilerResource extends CompilerResourceBase {
  static final String COMPILER_PATH = "/compile";

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public CompilationReport compilePost(@RequestBody List<JavaSourceStringDto> javaSources) {
    throwBadRequestIfSourcesAreInvalid(javaSources);
    CompilationReportBuilder reportBuilder = new CompilationReportBuilder();
    compile(javaSources, reportBuilder);

    return reportBuilder.buildReport();
  }

}
