package no.rosbach.jcoru.compile;

import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.filemanager.JavaSourceString;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.LinkedList;

import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSource;
import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSources;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JUnitTestRunnerTest extends JUnitRunnerTestBase {

  @Test
  public void testAcceptsSuccessTestClassesAndReturnsResult() throws IOException, ClassNotFoundException {
    Result result = runTests(getFixtureSource(Fixtures.SUCCESS_TEST));
    assertTrue(result.wasSuccessful());
  }

  @Test
  public void testAcceptsFailTestClassesAndReturnsResult() {
    Result result = runTests(getFixtureSource(Fixtures.FAIL_TEST));
    assertFalse(result.wasSuccessful());
  }

  @Test
  public void testAcceptsListOfTestClassesAndReturnsResult() {
    Result result = runTests(getFixtureSources(Fixtures.FAIL_TEST, Fixtures.SUCCESS_TEST));
    assertFalse(result.wasSuccessful());
    assertTrue(result.getRunCount() > 1);
  }

  @Test
  public void testAcceptsEmptyList() {
    Result result = runTests(new LinkedList<JavaSourceString>());
    assertEquals(0, result.getRunCount());
  }

  @Test
  public void testAcceptsMixOfTestAndNonTestClasses() {
    Result result = runTests(getFixtureSources(Fixtures.TEST_SUBJECT, Fixtures.TEST_SUBJECT_TEST));
    assertEquals(1, result.getRunCount());
    assertTrue(result.wasSuccessful());
  }

}
