package no.rosbach.jcoru.compile;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.utils.Stream.stream;

import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.rest.reports.CompilationReportBuilder;
import no.rosbach.jcoru.rest.reports.JUnitReport;
import no.rosbach.jcoru.rest.reports.Report;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.List;

public class JUnitTestRunner implements Runnable {
  public static final String TEST_CLASS_NAME_POSTFIX = "Test";
  private final CompilationReportBuilder reportBuilder = new CompilationReportBuilder();
  private final JavaCompiler compiler = new JavaCompiler(reportBuilder);
  private final JUnitCore core = new JUnitCore();
  private final List<CompiledClassObject> javaClasses;
  private Report report;

  public JUnitTestRunner(List<CompiledClassObject> javaClasses) {
    this.javaClasses = javaClasses;
  }

  @Override
  public void run() {
    List<Class> collect = stream(javaClasses).map(c -> loadClass(c)).filter(c -> c.getSimpleName().endsWith(TEST_CLASS_NAME_POSTFIX)).collect(toList());
    Result testResult = runTests(collect);
    this.report = new Report(new JUnitReport(testResult));
  }

  public Report getReport() {
    return report;
  }

  private Result runTests(List<Class> classes) {
    return core.run(classes.toArray(new Class[]{}));
  }

  private Class<?> loadClass(CompiledClassObject javaFile) {
    try {
      return compiler.getClassLoader().loadClass(javaFile.getName());
    } catch (ClassNotFoundException e) {
      throw new NonRecoverableError("Could not load class: " + javaFile.getName(), e);
    }
  }
}
