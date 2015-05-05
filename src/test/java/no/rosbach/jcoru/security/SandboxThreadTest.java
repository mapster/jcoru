package no.rosbach.jcoru.security;

import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSources;

import no.rosbach.jcoru.compile.JUnitTestRunner;
import no.rosbach.jcoru.compile.JavaCompileUtil;
import no.rosbach.jcoru.compile.SensitiveDiagnosticListener;
import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.filemanager.JavaSourceString;

import org.junit.Test;

import java.util.List;

public class SandboxThreadTest extends SandboxTestBase {

  private ClassLoader classLoader;

  @Test
  public void canRun_createStringVariable() {
    runInSandbox(
        () -> {
          String s = "";
        }
    );
    assertNoErrors();
  }

  @Test
  public void canRun_Sysout() {
    runInSandbox(
        () -> {
          System.out.println("hello");
        }
    );
    assertNoErrors();
  }

  @Test
  public void catchesSecurityExceptionsFromTarget() {
    runInSandbox(
        () -> {
          System.exit(1);
        }
    );
    assertTargetThrew(StrictAccessControlException.class);
  }


  @Test
  public void testThatExceptionsFromSandboxEnvIsCaught() {
    runInSandbox(
        null,
        () -> {
        }
    );
    assertSandBoxThrew(NullPointerException.class);
  }

  @Test
  public void ableToRunTestRunner() {
    runInSandbox(JUnitTestRunner.getRunner(compile(getFixtureSources(Fixtures.TEST_SUBJECT_TEST, Fixtures.TEST_SUBJECT)), classLoader));
    assertNoErrors();
  }

  public List<CompiledClassObject> compile(List<JavaSourceString> sources) {
    JavaCompileUtil compiler = new JavaCompileUtil(new SensitiveDiagnosticListener());
    classLoader = compiler.getClassLoader();
    return compiler.compile(sources);
  }
}
