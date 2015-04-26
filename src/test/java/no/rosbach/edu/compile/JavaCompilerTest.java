package no.rosbach.edu.compile;

import com.sun.tools.javac.util.ClientCodeException;
import no.rosbach.edu.compile.fixtures.Fixtures;
import no.rosbach.edu.filemanager.JavaSourceString;
import org.junit.Before;
import org.junit.Test;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mapster on 25.11.14.
 */
public class JavaCompilerTest {

    private static final JavaSourceString MY_TEST_SOURCE = new JavaSourceString("MyTest.java", "public class MyTest { public String test(String arg) { return (\"Hello world \" + arg); }}");
    private static final JavaSourceString AGGREGATION_CLASS_SOURCE = new JavaSourceString("Aggregation.java", "public class Aggregation { Contained value = new Contained(); public Contained getValue() { return value; } public String getContainedValue() { return value.getActualValue(); } }");
    private static final JavaSourceString CONTAINED_CLASS_SOURCE = new JavaSourceString("Contained.java", "public class Contained { public String getActualValue() { return \"the actual value\"; } }");
    private JavaCompiler compiler;

    @Before
    public void setStage() {
        compiler = new JavaCompiler(new SensitiveDiagnosticListener());
    }

    @Test
    public void ableToCompileFromString() {
        JavaFileObject compiled = compile(MY_TEST_SOURCE).iterator().next();
        assertEquals(Kind.CLASS, compiled.getKind());
        assertEquals("MyTest", compiled.toUri().toString());
    }

    @Test
    public void compilerUsesDiagnosticListener() throws IOException {
        try {
            compile(new JavaSourceString("WrongName.java", MY_TEST_SOURCE.getCharContent(true).toString()));
        } catch (ClientCodeException ex) {
            assertTrue(ex.getCause() instanceof SensitiveDiagnosticListener.CompilationError);
        }
    }

    @Test
    public void ableToCompileAggregationClass() {
        Iterable<? extends JavaFileObject> compiled = compile(AGGREGATION_CLASS_SOURCE, CONTAINED_CLASS_SOURCE);
        assertEquals(2, collect(compiled).size());
    }

    @Test
    public void ableToCompileUnitTestClass() {
        Iterable<? extends JavaFileObject> compiled = compile(getFixtureSource(Fixtures.FAIL_TEST));
        assertEquals(Fixtures.FAIL_TEST.toString(), compiled.iterator().next().getName());
    }

    @Test
    public void ableToCompileSingleClass() {
        JavaFileObject compiled = compiler.compile(getFixtureSource(Fixtures.FAIL_TEST)).iterator().next();
        assertEquals(Fixtures.FAIL_TEST.toString(), compiled.getName());
    }

    private Iterable<? extends JavaFileObject> compile(JavaSourceString... source) {
        return compiler.compile(Arrays.asList(source));
    }


    private static <T> List<T> collect(Iterable<T> it) {
        List<T> list = new LinkedList<>();
        for(T e: it) {
            list.add(e);
        }
        return list;
    }
}
