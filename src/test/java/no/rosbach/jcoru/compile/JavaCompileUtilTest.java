package no.rosbach.jcoru.compile;

import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.filemanager.InMemoryClassFile;
import no.rosbach.jcoru.filemanager.JavaSourceString;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

/**
 * Created by mapster on 25.11.14.
 */
public class JavaCompileUtilTest {

  private static final JavaSourceString MY_TEST_SOURCE = new JavaSourceString(
      "MyTest.java",
      "public class MyTest { public String test(String arg) { return (\"Hello world \" + arg); }}");
  private static final JavaSourceString AGGREGATION_CLASS_SOURCE = new JavaSourceString(
      "Aggregation.java",
      "public class Aggregation { Contained value = new Contained(); public Contained getValue() { return value; } public String getContainedValue() { return value.getActualValue(); } }");
  private static final JavaSourceString CONTAINED_CLASS_SOURCE = new JavaSourceString(
      "Contained.java",
      "public class Contained { public String getActualValue() { return \"the actual value\"; } }");
  private JavaCompileUtil compiler;

  private static <T> List<T> collect(Iterable<T> it) {
    List<T> list = new LinkedList<>();
    for (T e : it) {
      list.add(e);
    }
    return list;
  }

  @Before
  public void setStage() {
    compiler = new JavaCompileUtil(new SensitiveDiagnosticListener());
  }

  @Test
  public void ableToCompileFromString() {
    JavaFileObject compiled = compile(MY_TEST_SOURCE).iterator().next();
    assertEquals(Kind.CLASS, compiled.getKind());
    assertEquals("MyTest", compiled.toUri().toString());
  }

  @Test
  public void compilerUsesDiagnosticListener() {
    try {
      compile(new JavaSourceString("WrongName.java", MY_TEST_SOURCE.getCharContent(true).toString()));
    } catch (RuntimeException ex) {
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
    JavaFileObject compiled = compile(getFixtureSource(Fixtures.FAIL_TEST)).iterator().next();
    assertEquals(Fixtures.FAIL_TEST.toString(), compiled.getName());
  }

  @Test
  public void objectsReturnedShouldBeInMemoryClassFilesWrappedIn_CompiledClassObject() {
    CompiledClassObject compiled = compile(getFixtureSource(Fixtures.TEST_SUBJECT)).get(0);
    assertEquals(InMemoryClassFile.class, compiled.getWrappedObject().getClass());
  }

  @Test
  public void packagedClassObjectShouldBeInMemoryClassFilesWrappedIn_CompiledClassObject() {
    CompiledClassObject compiled = compile(getFixtureSource(Fixtures.PACKAGED_CLASS)).get(0);
    assertEquals(InMemoryClassFile.class, compiled.getWrappedObject().getClass());
  }

  @Test
  public void shouldReturnEmptyListIfNoSourcesArePassed() {
    assertTrue(compile().isEmpty());
  }


  //TODO: Test that all declared libraries are loaded as classpath args
  //TODO: Test that an empty list of libraries also works
  @Test
  @Ignore
  public void shouldAddAllLibJarsToClassPath() {
    javax.tools.JavaCompiler mock = mock(javax.tools.JavaCompiler.class);
    compiler = new JavaCompileUtil(mock, new SensitiveDiagnosticListener());
    compiler.compile(getFixtureSource(Fixtures.TEST_CLASS));

    verify(mock).getTask(
        any(PrintWriter.class), any(JavaFileManager.class), any(DiagnosticListener.class), argThat(
            new ArgumentMatcher<Iterable<String>>() {
              @Override
              public boolean matches(Object o) {
                Iterator<String> iterator = ((Iterable<String>) o).iterator();
                assertEquals("-classpath", iterator.next());
                return false;
              }
            }), any(Iterable.class), any(Iterable.class));
  }

  private List<CompiledClassObject> compile(JavaSourceString... source) {
    return compiler.compile(Arrays.asList(source));
  }
}
