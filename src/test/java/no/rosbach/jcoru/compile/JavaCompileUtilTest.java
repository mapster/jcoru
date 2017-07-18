package no.rosbach.jcoru.compile;

import com.sun.source.util.JavacTask;
import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.filemanager.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.*;
import java.util.*;

import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
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

  @Resource
  private JavaCompileUtil javaCompileUtil;
  @Resource
  private InMemoryFileManager inMemoryFileManager;

  private static <T> List<T> collect(Iterable<T> it) {
    List<T> list = new LinkedList<>();
    for (T e : it) {
      list.add(e);
    }
    return list;
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

  @Test
  public void shouldAddAllLibJarsToClassPath() {
    JavaCompiler mock = mock(JavaCompiler.class);
    when(
        mock.getTask(
            any(Writer.class),
            any(JavaFileManager.class),
            any(DiagnosticListener.class),
            any(Iterable.class),
            any(Iterable.class),
            any(Iterable.class)))
        .thenReturn(mock(JavacTask.class));

    javaCompileUtil = new JavaCompileUtil(mock, inMemoryFileManager);
    javaCompileUtil.compile(getFixtureSource(Fixtures.TEST_CLASS), new SensitiveDiagnosticListener());

    verify(mock).getTask(
        any(PrintWriter.class), any(JavaFileManager.class), any(DiagnosticListener.class), argThat(
            new ArgumentMatcher<Iterable<String>>() {
              @Override
              public boolean matches(Object o) {
                File[] libs = new File(this.getClass().getClassLoader().getResource(JavaCompileUtil.LIB_RESOURCE_DIRECTORY).getFile()).listFiles();
                Iterator<String> iterator = ((Iterable<String>) o).iterator();

                // if no libs, then options should be empty.
                if (libs.length == 0) {
                  return !iterator.hasNext();
                }

                // verify starts with -classpath
                if (!"-classpath".equals(iterator.next())) {
                  return false;
                }

                // build set of classpath entries
                HashSet<String> cp = new HashSet<String>();
                cp.addAll(Arrays.asList(iterator.next().split(":")));

                // verify that all files in lib dir are in classpath
                for (File lib : libs) {
                  if (!cp.contains(lib.getPath())) {
                    return false;
                  }
                }
                return true;
              }
            }), any(Iterable.class), any(Iterable.class));
  }

  private List<CompiledClassObject> compile(JavaSourceString... source) {
    return javaCompileUtil.compile(Arrays.asList(source), new SensitiveDiagnosticListener());
  }
}
