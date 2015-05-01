package no.rosbach.jcoru.compile;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.utils.Stream.stream;

import no.rosbach.jcoru.filemanager.CompiledClassObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.List;

public class JUnitTestRunner implements Runnable {
  public static final String TEST_CLASS_NAME_POSTFIX = "Test";
  private static final Logger LOGGER = LogManager.getLogger();
  private final JUnitCore core = new JUnitCore();
  private final List<Class> loadedClasses;
  private final ClassLoader classLoader;
  private List<Class> testClasses;
  private Result result;

  private JUnitTestRunner(List<Class> javaClasses, List<Class> testClasses, ClassLoader classLoader) {
    this.loadedClasses = javaClasses;
    this.testClasses = testClasses;
    this.classLoader = classLoader;
  }

  public static JUnitTestRunner getRunner(List<CompiledClassObject> javaClasses, ClassLoader classLoader) {
    List<Class> loadedClasses = stream(javaClasses).map(c -> loadClass(c, classLoader)).collect(toList());
    List<Class> testClasses = loadedClasses.stream().filter(c -> c.getSimpleName().endsWith(TEST_CLASS_NAME_POSTFIX)).collect(toList());
    return new JUnitTestRunner(loadedClasses, testClasses, classLoader);
  }

  private static Class<?> loadClass(CompiledClassObject javaFile, ClassLoader classLoader) {
    try {
      return classLoader.loadClass(javaFile.getName());
    } catch (ClassNotFoundException e) {
      throw new NonRecoverableError("Could not load class: " + javaFile.getName(), e);
    }
  }

  @Override
  public void run() {
    this.result = core.run(testClasses.toArray(new Class[]{}));
  }

  public Result getResult() {
    return result;
  }
}
