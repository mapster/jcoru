package no.rosbach.jcoru.compile;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.utils.Stream.stream;

import no.rosbach.jcoru.filemanager.JavaSourceString;
import no.rosbach.jcoru.rest.reports.CompilationReport;
import no.rosbach.jcoru.rest.reports.CompilationReportBuilder;
import no.rosbach.jcoru.rest.reports.JUnitReport;
import no.rosbach.jcoru.rest.reports.Report;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.List;
import java.util.stream.Stream;

import javax.tools.JavaFileObject;

public class JUnitTestRunner implements Runnable {
  public static final String TEST_CLASS_NAME_POSTFIX = "Test";
  private final CompilationReportBuilder reportBuilder = new CompilationReportBuilder();
  private final JavaCompiler compiler = new JavaCompiler(reportBuilder);
  private final JUnitCore core = new JUnitCore();
  private final List<JavaSourceString> javaSources;
  private Report report;

  public JUnitTestRunner(List<JavaSourceString> javaSources) {
    this.javaSources = javaSources;
  }

  @Override
  public void run() {
    Iterable<? extends JavaFileObject> compiledClasses = compiler.compile(javaSources);

    // If compilation failed the return compilation report
    CompilationReport compilationReport = reportBuilder.buildReport();
    if (!compilationReport.isSuccess()) {
      this.report = new Report(compilationReport);
    } else {
      Stream<? extends Class<?>> loadedClasses = stream(compiledClasses).map(c -> loadClass(c));
      Result testResult = runTests(loadedClasses.filter(c -> c.getSimpleName().endsWith(TEST_CLASS_NAME_POSTFIX)).collect(toList()));
      this.report = new Report(new JUnitReport(testResult));
    }
  }

  public Report getReport() {
    return report;
  }

  private Result runTests(List<Class> classes) {
    return core.run(classes.toArray(new Class[]{}));
  }

  private Class<?> loadClass(JavaFileObject javaFile) {
    try {
      return compiler.getClassLoader().loadClass(javaFile.getName());
    } catch (ClassNotFoundException e) {
      throw new NonRecoverableError("Could not load class: " + javaFile.getName(), e);
    }
  }
}
