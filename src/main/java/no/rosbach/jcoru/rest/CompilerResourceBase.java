package no.rosbach.jcoru.rest;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import no.rosbach.jcoru.compile.JavaCompileUtil;
import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.rest.reports.CompilationReportBuilder;

import java.util.List;

import javax.tools.ToolProvider;
import javax.ws.rs.BadRequestException;

public abstract class CompilerResourceBase {
  protected final CompilationReportBuilder reportBuilder = new CompilationReportBuilder();
  private JavaCompileUtil compiler = new JavaCompileUtil(ToolProvider.getSystemJavaCompiler());

  protected void throwBadRequestIfSourcesAreInvalid(List<JavaSourceStringDto> sources) {
    if (sources == null) {
      throw new BadRequestException("Empty source payload.");
    }
    List<JavaSourceStringDto> invalidSources = sources.stream().filter(s -> isBlank(s.filename) || isBlank(s.sourcecode)).collect(toList());
    if (!invalidSources.isEmpty()) {
      throw new BadRequestException(
          String.format(
              "The following source files are missing file name or source code: ",
              invalidSources.stream().map(s -> s.filename).reduce((f1, f2) -> f1 + ", " + f2)));
    }
  }

  protected List<CompiledClassObject> compile(List<JavaSourceStringDto> sources) {
    return compiler.compile(sources.stream().map(source -> source.transfer()).collect(toList()), reportBuilder);
  }

  protected ClassLoader getClassLoader() {
    return compiler.getClassLoader();
  }
}
