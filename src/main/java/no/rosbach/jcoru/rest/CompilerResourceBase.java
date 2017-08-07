package no.rosbach.jcoru.rest;

import no.rosbach.jcoru.compile.JavaCompileUtil;
import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.rest.reports.*;

import javax.annotation.Resource;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class CompilerResourceBase {

  @Resource
  private JavaCompileUtil javaCompileUtil;

  protected void throwBadRequestIfSourcesAreInvalid(List<JavaSourceStringDto> sources) {
    if (sources == null) {
      throw new BadRequestException("Empty source payload.");
    }
    List<JavaSourceStringDto> invalidSources = sources.stream().filter(s -> isBlank(s.filename) || isBlank(s.sourcecode)).collect(toList());
    if (!invalidSources.isEmpty()) {
      throw new BadRequestException(
          String.format(
              "The following source files are missing file name or source code: %s",
              invalidSources.stream().map(s -> s.filename).reduce((f1, f2) -> f1 + ", " + f2)));
    }
  }

  protected List<CompiledClassObject> compile(List<JavaSourceStringDto> sources, CompilationReportBuilder reportBuilder) {
    return javaCompileUtil.compile(sources.stream().map(JavaSourceStringDto::transfer).collect(toList()), reportBuilder);
  }

  protected ClassLoader getClassLoader() {
    return javaCompileUtil.getClassLoader();
  }
}
