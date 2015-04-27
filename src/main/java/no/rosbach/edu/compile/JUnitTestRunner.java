package no.rosbach.edu.compile;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mapster on 06.04.15.
 */
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
