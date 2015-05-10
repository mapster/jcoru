package no.rosbach.jcoru.compile;

import static org.junit.Assert.fail;

import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.filemanager.InMemoryFileManager;
import no.rosbach.jcoru.filemanager.JavaSourceString;
import no.rosbach.jcoru.provider.WhitelistProvider;
import no.rosbach.jcoru.rest.reports.CompilationReport;
import no.rosbach.jcoru.rest.reports.CompilationReportBuilder;

import org.junit.runner.Result;

import java.util.Arrays;
import java.util.List;

import javax.tools.ToolProvider;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitRunnerTestBase {
  WhitelistProvider provider = new WhitelistProvider();

  protected Result runTests(JavaSourceString fixtureSource) {
    return runTests(Arrays.asList(fixtureSource));
  }

  protected Result runTests(List<JavaSourceString> fixtureSources) {
    CompilationReportBuilder reportBuilder = new CompilationReportBuilder();
    JavaCompileUtil compiler = new JavaCompileUtil(
        ToolProvider.getSystemJavaCompiler(),
        new InMemoryFileManager(new TransientClassLoader(provider.getClassloaderWhitelist()), provider.getFileManagerPackagesWhitelist()));

    List<CompiledClassObject> compiledClasses = compiler.compile(fixtureSources, reportBuilder);

    // If compilation failed the return compilation report
    CompilationReport compilationReport = reportBuilder.buildReport();
    if (!compilationReport.isSuccess()) {
      fail("Failed to compile fixtures.");
    }

    JUnitTestRunner testRunner = JUnitTestRunner.getRunner(compiledClasses, compiler.getClassLoader());
    testRunner.run();
    return testRunner.getResult();
  }
}
