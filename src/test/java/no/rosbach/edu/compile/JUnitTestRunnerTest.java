package no.rosbach.edu.compile;

import no.rosbach.edu.compile.fixtures.Fixtures;
import no.rosbach.edu.filemanager.JavaSourceString;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.stream.StreamSupport;

/**
 * Created by mapster on 06.04.15.
 */
public class JUnitTestRunnerTest {

    @Test
    public void test() throws IOException, ClassNotFoundException {
        JUnitCore core = new JUnitCore();
        MyCompiler compiler = new MyCompiler();
        JavaSourceString fixtureSource = Fixtures.getFixtureSource(Fixtures.UNIT_TEST);
        ClassLoader classLoader = compiler.getClassLoader();
        Class<?>[] classes = StreamSupport.stream(compiler.compile(fixtureSource).spliterator(), false).map(javaFile -> loadClass(classLoader, javaFile)).toArray(Class<?>[]::new);


        Result run = core.run(classes);
    }

    private Class<?> loadClass(ClassLoader classLoader, JavaFileObject javaFile) {
        try {
            return classLoader.loadClass(javaFile.getName());
        } catch (ClassNotFoundException e) {
            throw new Error("Could not load class: "+javaFile.getName(), e);
        }
    }


}
