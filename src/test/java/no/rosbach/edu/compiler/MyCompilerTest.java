package no.rosbach.edu.compiler;

import no.rosbach.edu.compiler.fixtures.AggregationClass;
import no.rosbach.edu.compiler.fixtures.ContainedClass;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by mapster on 25.11.14.
 */
public class MyCompilerTest {

    static final JavaSourceString MY_TEST_SOURCE = new JavaSourceString("MyTest.java", "public class MyTest { public String test(String arg) { return (\"Hello world \" + arg); }}");
    private MyCompiler compiler;

    @Before
    public void setStage() {
        compiler = new MyCompiler();
    }

    @Test
    public void ableToCompileFromString() {
        JavaFileObject compiled = compile(MY_TEST_SOURCE).iterator().next();
        assertEquals(Kind.CLASS, compiled.getKind());
        assertEquals("MyTest", compiled.toUri().toString());
    }

    @Test
    public void ableToCompileAggregationClass() {
        compiler.fileManager = new InMemoryFileManager(Arrays.asList(getFixtureSource(AggregationClass.class), getFixtureSource(ContainedClass.class)));
        Iterable<? extends JavaFileObject> compiled = compile(getFixtureSource(AggregationClass.class));
        compiled.iterator().hasNext();
    }

    private void runMain(Iterable<? extends JavaFileObject> compiled, ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        JavaFileObject obj = compiled.iterator().next();
        Class<?> MyTestClass = ((TransientClassLoader)classLoader).loadClass(obj.toUri().toString());

        Method main = MyTestClass.getMethod("main", String[].class);
        String[] params = {"alexander", "per"};
        main.invoke(null, (Object) params);
    }

    private Iterable<? extends JavaFileObject> compile(JavaSourceString source) {
        try {
            return compiler.compile(source);
        } catch (IOException e) {
            throw new Error("Compilation failed.", e);
        }
    }

    private JavaSourceString getFixtureSource(Class<?> fixtureInterface) {
        String name = fixtureInterface.getSimpleName() + ".java";
        try(InputStream sourceStream = this.getClass().getClassLoader().getResourceAsStream("fixtures" + File.separatorChar + name)) {
            return new JavaSourceString(name, IOUtils.toString(sourceStream));
        } catch (IOException e) {
            throw new Error("Failed to read fixture java source.", e);
        }
    }
}
