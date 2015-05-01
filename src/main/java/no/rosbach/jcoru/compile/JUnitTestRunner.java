package no.rosbach.jcoru.compile;

import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.rest.reports.JUnitReport;
import no.rosbach.jcoru.rest.reports.Report;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.LinkedList;
import java.util.List;

public class JUnitTestRunner implements Runnable {
  public static final String TEST_CLASS_NAME_POSTFIX = "Test";
  private final JUnitCore core = new JUnitCore();
  private final List<CompiledClassObject> javaClasses;
  private final ClassLoader classLoader;
  private Report report;

  public JUnitTestRunner(List<CompiledClassObject> javaClasses, ClassLoader classLoader) {
    this.javaClasses = javaClasses;
    this.classLoader = classLoader;
  }

  @Override
  public void run() {
//    List<Class> collect = stream(javaClasses).map(c -> loadClass(c)).filter(c -> c.getSimpleName().endsWith(TEST_CLASS_NAME_POSTFIX)).collect(toList());
    List<Class> testClasses = new LinkedList<>();
    for (CompiledClassObject clazz : javaClasses) {
      Class<?> loadedClass = loadClass(clazz);
      if (loadedClass.getSimpleName().endsWith(TEST_CLASS_NAME_POSTFIX)) {
        testClasses.add(loadedClass);
      }
    }

    Result testResult = runTests(testClasses);
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
      return classLoader.loadClass(javaFile.getName());
    } catch (ClassNotFoundException e) {
      throw new NonRecoverableError("Could not load class: " + javaFile.getName(), e);
    }
  }
}
