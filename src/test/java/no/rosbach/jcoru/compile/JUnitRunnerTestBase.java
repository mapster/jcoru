package no.rosbach.jcoru.compile;

import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.filemanager.JavaSourceString;
import no.rosbach.jcoru.rest.reports.CompilationReport;
import no.rosbach.jcoru.rest.reports.CompilationReportBuilder;
import org.junit.runner.Result;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

@SpringBootTest
public class JUnitRunnerTestBase {

  @Resource
  private JavaCompileUtil javaCompileUtil;

  protected Result runTests(JavaSourceString fixtureSource) {
    return runTests(Arrays.asList(fixtureSource));
  }

  protected Result runTests(List<JavaSourceString> fixtureSources) {
    CompilationReportBuilder reportBuilder = new CompilationReportBuilder();

    List<CompiledClassObject> compiledClasses = javaCompileUtil.compile(fixtureSources, reportBuilder);

    // If compilation failed the return compilation report
    CompilationReport compilationReport = reportBuilder.buildReport();
    if (!compilationReport.isSuccess()) {
      fail("Failed to compile fixtures.");
    }

    JUnitTestRunner testRunner = JUnitTestRunner.getRunner(compiledClasses, javaCompileUtil.getClassLoader());
    testRunner.run();
    return testRunner.getResult();
  }
}
