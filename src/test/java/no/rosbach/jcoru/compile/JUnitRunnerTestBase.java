package no.rosbach.jcoru.compile;

import no.rosbach.jcoru.filemanager.JavaSourceString;
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
    JUnitTestRunner testRunner = new JUnitTestRunner(fixtureSources);
    testRunner.run();
    return testRunner.getReport();
  }
}
