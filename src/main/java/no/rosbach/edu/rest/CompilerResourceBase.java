package no.rosbach.edu.rest;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import no.rosbach.edu.compile.JavaCompiler;
import no.rosbach.edu.rest.reports.CompilationReportBuilder;

import java.util.List;

import javax.tools.JavaFileObject;
import javax.ws.rs.BadRequestException;

/**
 * Created by mapster on 26.04.15.
 */
public abstract class CompilerResourceBase {
  protected final CompilationReportBuilder reportBuilder = new CompilationReportBuilder();
  private final JavaCompiler compiler = new JavaCompiler(reportBuilder);
  private final ClassLoader classLoader = compiler.getClassLoader();

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

  protected Iterable<? extends JavaFileObject> compile(List<JavaSourceStringDto> sources) {
    return compiler.compile(sources.stream().map(source -> source.transfer()).collect(toList()));
  }

  protected Class<?> loadClass(JavaFileObject javaFile) {
    try {
      return classLoader.loadClass(javaFile.getName());
    } catch (ClassNotFoundException e) {
      throw new Error("Could not load class: " + javaFile.getName(), e);
    }
  }
}
