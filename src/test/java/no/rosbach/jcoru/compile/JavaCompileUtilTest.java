package no.rosbach.jcoru.compile;

import com.sun.source.util.JavacTask;
import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.filemanager.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.File;
import java.io.Writer;
import java.util.*;

import static java.util.stream.Collectors.toSet;
import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = {"compilerLibsPath=target/test-classes/lib"})
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableConfigurationProperties
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

  @MockBean
  private JavaCompiler javaCompiler;
  @Resource
  private JavaCompileUtil javaCompileUtil;
  @Value("${compilerLibsPath}")
  private String compilerLibsPath;

  @Captor
  private ArgumentCaptor<Iterable<String>> iterableStringCaptor;

  @Before
  public void setUp() {
    JavaCompiler systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
    when(javaCompiler.getTask(any(), any(), any(), any(), any(), any())).thenAnswer(invocation -> {
      Writer out = invocation.getArgumentAt(0, Writer.class);
      JavaFileManager fileManager = invocation.getArgumentAt(1, JavaFileManager.class);
      DiagnosticListener diagnosticListener = invocation.getArgumentAt(2, DiagnosticListener.class);
      Iterable options = invocation.getArgumentAt(3, Iterable.class);
      Iterable classes = invocation.getArgumentAt(4, Iterable.class);
      Iterable compilationUnits = invocation.getArgumentAt(5, Iterable.class);
      return systemJavaCompiler.getTask(out, fileManager, diagnosticListener, Collections::emptyIterator, classes, compilationUnits);
    });
  }

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
    when(javaCompiler.getTask(any(), any(), any(), any(), any(), any())).thenReturn(mock(JavacTask.class));

    javaCompileUtil.compile(getFixtureSource(Fixtures.TEST_CLASS), new SensitiveDiagnosticListener());

    verify(javaCompiler).getTask(any(), any(), any(), iterableStringCaptor.capture(), any(), any());
    Iterator<String> actualLibs = iterableStringCaptor.getValue().iterator();

    assertEquals("-classpath", actualLibs.next());

    Set<String> actualClasspath = Arrays.stream(actualLibs.next().split(":")).collect(toSet());
    Set<String> expectedLibs = Arrays.stream(new File(compilerLibsPath).listFiles()).map(File::getPath).collect(toSet());
    assertEquals(expectedLibs, actualClasspath);
  }

  private List<CompiledClassObject> compile(JavaSourceString... source) {
    return javaCompileUtil.compile(Arrays.asList(source), new SensitiveDiagnosticListener());
  }
}
