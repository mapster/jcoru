package no.rosbach.edu.compile;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.Arrays;
import java.util.List;

public class JUnitTestRunner {
  private JUnitCore core = new JUnitCore();

  public JUnitTestRunner() {

  }

  public Result test(Class clazz) {
    return test(Arrays.asList(clazz));
  }

  public Result test(List<Class> classes) {
    return core.run(classes.toArray(new Class[]{}));
  }

}
