package no.rosbach.jcoru.security;

import no.rosbach.jcoru.compile.*;
import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.filemanager.JavaSourceString;
import no.rosbach.jcoru.provider.JavaCompilerProvider;
import no.rosbach.jcoru.provider.WhitelistProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static no.rosbach.jcoru.compile.fixtures.Fixtures.getFixtureSources;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SandboxThreadTest extends SandboxTestBase {
  private final WhitelistProvider whitelistProvider = new WhitelistProvider();
  private final JavaCompilerProvider compilerProvider = new JavaCompilerProvider();

  @Resource
  private JavaCompileUtil javaCompileUtil;

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
        () -> {}
    );
    assertSandBoxThrew(NullPointerException.class);
  }

  @Test
  public void ableToRunTestRunner() {
    runInSandbox(JUnitTestRunner.getRunner(compile(getFixtureSources(Fixtures.TEST_SUBJECT_TEST, Fixtures.TEST_SUBJECT)), classLoader));
    assertNoErrors();
  }

  public List<CompiledClassObject> compile(List<JavaSourceString> sources) {
    classLoader = javaCompileUtil.getClassLoader();
    return javaCompileUtil.compile(sources, new SensitiveDiagnosticListener());
  }
}
