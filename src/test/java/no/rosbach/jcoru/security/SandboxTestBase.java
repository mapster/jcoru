package no.rosbach.jcoru.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SandboxTestBase {
  private SandboxThread sandbox;

  protected void assertTargetThrew(Class type) {
    assertEquals(type, sandbox.getThrownByTarget().getClass());
  }

  protected void assertNoErrors() {
    Throwable byTarget = sandbox.getThrownByTarget();
    assertNull("Target threw an exception: " + byTarget, byTarget);
    Throwable bySandbox = sandbox.getThrownBySandbox();
    assertNull("Sandbox threw an exception: " + bySandbox, bySandbox);
  }

  protected void runInSandbox(Runnable target) {
    this.sandbox = new SandboxThread(target);
    runSandbox();
  }

  protected void runSandbox() {
    sandbox.start();
    try {
      sandbox.join();
    } catch (InterruptedException e) {
      throw new Error("SandboxThread interrupted.", e);
    }
  }

  protected void runInSandbox(StrictSecurityManager sm, Runnable target) {
    this.sandbox = new SandboxThread(sm, target);
    runSandbox();
  }

  protected void assertSandBoxThrew(Class<? extends Throwable> expected) {
    assertNotNull(sandbox.getThrownBySandbox());
    assertEquals(expected, sandbox.getThrownBySandbox().getClass());
  }
}
