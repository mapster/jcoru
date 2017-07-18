package no.rosbach.jcoru.rest.facade;

import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.rest.JavaSourceStringDto;
import no.rosbach.jcoru.rest.reports.BadRequestException;
import no.rosbach.jcoru.rest.reports.CompilationReport;
import org.junit.Test;

import java.util.*;

import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureInterfaceSource;
import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertFalse;

public abstract class CompilerResourceTestBase {

  static final JavaSourceStringDto TEST_CLASS_SOURCE = new JavaSourceStringDto(getFixtureSource(Fixtures.TEST_CLASS));
  static final JavaSourceStringDto TEST_CLASS_I_SOURCE = new JavaSourceStringDto(getFixtureInterfaceSource(Fixtures.TEST_CLASS));

  protected abstract CompilationReport compile(List<JavaSourceStringDto> sources);

  protected CompilationReport compile(JavaSourceStringDto source) {
    return compile(Arrays.asList(source));
  }

  @Test
  public void compilerReturnsFailedReport() {
    CompilationReport result = compile(TEST_CLASS_SOURCE);
    assertFalse(result.isSuccess());
  }

  @Test(expected = BadRequestException.class)
  public void returnsBadRequestForJavaFileMissingSourceString() {
    compile(new JavaSourceStringDto("TestClass.java", null));
  }

  @Test(expected = BadRequestException.class)
  public void returnsBadRequestForJavaFileMissingFileName() {
    compile(new JavaSourceStringDto("", "This is some java source"));
  }

  @Test(expected = BadRequestException.class)
  public void returnsBadRequestForNullPost() {
    compile((List<JavaSourceStringDto>) null);
  }

  @Test
  public void returnsFailingReportForIllegalSyntax() {
    CompilationReport report = compile(new JavaSourceStringDto(getFixtureSource(Fixtures.ILLEGAL_SYNTAX)));
    assertFalse(report.isSuccess());
  }

  @Test
  public void returnsFailingReportWhenReferencedClassIsMissing() {
    CompilationReport compilationReport = compile(new JavaSourceStringDto(getFixtureSource(Fixtures.TEST_SUBJECT_TEST)));
    assertFalse(compilationReport.isSuccess());
  }
}
