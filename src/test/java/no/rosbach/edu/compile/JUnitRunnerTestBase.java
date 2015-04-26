package no.rosbach.edu.compile;

import no.rosbach.edu.filemanager.JavaSourceString;
import org.junit.Before;
import org.junit.runner.Result;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

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

    public Result runTests(Class clazz) {
        return testRunner.test(clazz);
    }

    public Result runTests(List<Class> classes) {
        return testRunner.test(classes);
    }
}
