package no.rosbach.edu.compile;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.rosbach.edu.utils.Stream.stream;

import no.rosbach.edu.filemanager.JavaSourceString;

import org.junit.Before;
import org.junit.runner.Result;

import java.util.List;

import javax.tools.JavaFileObject;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitRunnerTestBase {
  private JUnitTestRunner testRunner;
  private JavaCompiler compiler;
  private ClassLoader classLoader;

  @Before
  public void prepareCompiler() {
    compiler = new JavaCompiler();
    classLoader = compiler.getClassLoader();
    testRunner = new JUnitTestRunner();
  }

  protected List<Class> compileAndLoadClasses(JavaSourceString... fixtureSource) {
    return stream(compiler.compile(asList(fixtureSource))).map(javaFile -> loadClass(javaFile)).collect(toList());
  }

  protected Class<?> loadClass(JavaFileObject javaFile) {
    try {
      return classLoader.loadClass(javaFile.getName());
    } catch (ClassNotFoundException e) {
      throw new Error("Could not load class: " + javaFile.getName(), e);
    }
  }

  public Result runTests(Class clazz) {
    return testRunner.test(clazz);
  }

  public Result runTests(List<Class> classes) {
    return testRunner.test(classes);
  }
}
