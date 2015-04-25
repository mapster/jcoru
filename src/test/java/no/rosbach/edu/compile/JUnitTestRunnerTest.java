package no.rosbach.edu.compile;

import no.rosbach.edu.compile.fixtures.Fixtures;
import no.rosbach.edu.filemanager.JavaSourceString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.*;

/**
 * Created by mapster on 06.04.15.
 */
public class JUnitTestRunnerTest {

    private JavaCompiler compiler;
    private ClassLoader classLoader;
    private JUnitTestRunner testRunner;

    @Before
    public void prepare() {
        compiler = new JavaCompiler();
        classLoader = compiler.getClassLoader();
        testRunner = new JUnitTestRunner();
    }

    @Test
    public void testAcceptsSuccessTestClassesAndReturnsResult() throws IOException, ClassNotFoundException {
        List<Class> classes = compileAndLoadClasses(getFixtureSource(Fixtures.SUCCESS_TEST));
        Result test = testRunner.test(classes);
        assertTrue(test.wasSuccessful());
    }

    @Test
    public void testAcceptsFailTestClassesAndReturnsResult() {
        List<Class> classes = compileAndLoadClasses(getFixtureSource(Fixtures.FAIL_TEST));
        Result test = testRunner.test(classes);
        assertFalse(test.wasSuccessful());
    }

    @Test
    public void testAcceptsListOfTestClassesAndReturnsResult() {
        List<Class> classes = compileAndLoadClasses(getFixtureSource(Fixtures.FAIL_TEST), getFixtureSource(Fixtures.SUCCESS_TEST));
        Result test = testRunner.test(classes);
        assertFalse(test.wasSuccessful());
    }

    @Test
    public void testAcceptsEmptyList() {
        Result r = testRunner.test(new LinkedList<>());
        assertEquals(0, r.getRunCount());
    }

    @Test
    public void testAcceptsMixOfTestAndNonTestClasses() {
        Class subjectClass = compileAndLoadClasses(getFixtureSource(Fixtures.TEST_SUBJECT)).get(0);
        Class testClass = compileAndLoadClasses(getFixtureSource(Fixtures.TEST_SUBJECT_TEST)).get(0);
        Result result = testRunner.test(testClass);
        assertEquals(1, result.getRunCount());
    }

    private List<Class> compileAndLoadClasses(JavaSourceString... fixtureSource) {
        try {
            return StreamSupport.stream(compiler.compile(Arrays.asList(fixtureSource)).spliterator(), false)
                    .map(javaFile -> loadClass(javaFile)).collect(toList());
        } catch (IOException e) {
            throw new Error("Failed to read fixture sources.", e);
        }
    }

    private Class<?> loadClass(JavaFileObject javaFile) {
        try {
            return classLoader.loadClass(javaFile.getName());
        } catch (ClassNotFoundException e) {
            throw new Error("Could not load class: "+javaFile.getName(), e);
        }
    }
}
