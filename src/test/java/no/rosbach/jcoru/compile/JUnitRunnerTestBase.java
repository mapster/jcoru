package no.rosbach.jcoru.compile;

import static org.junit.Assert.fail;

import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.filemanager.JavaSourceString;
import no.rosbach.jcoru.rest.reports.CompilationReport;
import no.rosbach.jcoru.rest.reports.CompilationReportBuilder;
import no.rosbach.jcoru.rest.reports.Report;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitRunnerTestBase {
  private JUnitTestRunner testRunner;

  protected Report runTests(JavaSourceString fixtureSource) {
    return runTests(Arrays.asList(fixtureSource));
  }

  protected Report runTests(List<JavaSourceString> fixtureSources) {
    CompilationReportBuilder reportBuilder = new CompilationReportBuilder();
    JavaCompiler compiler = new JavaCompiler(reportBuilder);

    List<CompiledClassObject> compiledClasses = compiler.compile(fixtureSources);

    // If compilation failed the return compilation report
    CompilationReport compilationReport = reportBuilder.buildReport();
    if (!compilationReport.isSuccess()) {
      fail("Failed to compile fixtures.");
    }

    JUnitTestRunner testRunner = new JUnitTestRunner(compiledClasses);
    testRunner.run();
    return testRunner.getReport();
  }
}
