package no.rosbach.jcoru.security;

import org.junit.Test;

public class SandboxThreadTest extends SandboxTestBase {

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

}
