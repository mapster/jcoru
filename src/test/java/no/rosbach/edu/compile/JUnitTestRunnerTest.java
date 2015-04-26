package no.rosbach.edu.compile;

import no.rosbach.edu.compile.fixtures.Fixtures;
import org.junit.Test;
import org.junit.runner.Result;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.*;

/**
 * Created by mapster on 06.04.15.
 */
public class JUnitTestRunnerTest extends JUnitRunnerTestBase {

    @Test
    public void testAcceptsSuccessTestClassesAndReturnsResult() throws IOException, ClassNotFoundException {
        List<Class> classes = compileAndLoadClasses(getFixtureSource(Fixtures.SUCCESS_TEST));
        Result test = runTests(classes);
        assertTrue(test.wasSuccessful());
    }

    @Test
    public void testAcceptsFailTestClassesAndReturnsResult() {
        List<Class> classes = compileAndLoadClasses(getFixtureSource(Fixtures.FAIL_TEST));
        Result test = runTests(classes);
        assertFalse(test.wasSuccessful());
    }

    @Test
    public void testAcceptsListOfTestClassesAndReturnsResult() {
        List<Class> classes = compileAndLoadClasses(getFixtureSource(Fixtures.FAIL_TEST), getFixtureSource(Fixtures.SUCCESS_TEST));
        Result test = runTests(classes);
        assertFalse(test.wasSuccessful());
    }

    @Test
    public void testAcceptsEmptyList() {
        Result r = runTests(new LinkedList<>());
        assertEquals(0, r.getRunCount());
    }

    @Test
    public void testAcceptsMixOfTestAndNonTestClasses() {
        Class subjectClass = compileAndLoadClasses(getFixtureSource(Fixtures.TEST_SUBJECT)).get(0);
        Class testClass = compileAndLoadClasses(getFixtureSource(Fixtures.TEST_SUBJECT_TEST)).get(0);
        Result result = runTests(testClass);
        assertEquals(1, result.getRunCount());
    }

}
