package no.rosbach.edu.compile;

import no.rosbach.edu.filemanager.JavaSourceString;
import no.rosbach.edu.rest.reports.Report;

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
    JUnitTestRunner testRunner = new JUnitTestRunner(fixtureSources);
    testRunner.run();
    return testRunner.getReport();
  }
}
