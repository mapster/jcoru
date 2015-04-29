package no.rosbach.jcoru.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by mapster on 29.04.15.
 */
public class SandboxThreadTest {

  private SandboxThread sandbox;

  @Before
  public void prepare() {
  }

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
  public void testThatAnyExceptionsFromSandboxEnvIsCaught() {
    runInSandbox(
        null,
        () -> {
        }
    );

    assertNotNull(sandbox.getThrownBySandbox());
    assertEquals(NullPointerException.class, sandbox.getThrownBySandbox().getClass());
  }

  private void assertTargetThrew(Class type) {
    assertEquals(type, sandbox.getThrownByTarget().getClass());
  }

  private void assertNoErrors() {
    Throwable byTarget = sandbox.getThrownByTarget();
    assertNull("Target threw an exception: " + byTarget, byTarget);
    Throwable bySandbox = sandbox.getThrownBySandbox();
    assertNull("Sandbox threw an exception: " + bySandbox, bySandbox);
  }

  private void runInSandbox(StrictSecurityManager smMock, Runnable target) {
    this.sandbox = new SandboxThread(smMock, target);
    runSandbox();
  }

  private void runInSandbox(Runnable target) {
    this.sandbox = new SandboxThread(target);
    runSandbox();
  }

  private void runSandbox() {
    sandbox.start();
    try {
      sandbox.join();
    } catch (InterruptedException e) {
      throw new Error("SandboxThread interrupted.", e);
    }
  }
}
