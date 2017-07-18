package no.rosbach.jcoru.security;

import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@SpringBootTest
class SandboxTestBase {
  private SandboxThread sandbox;

  @Resource
  private StrictSecurityManager strictSecurityManager;

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
    this.sandbox = new SandboxThread(strictSecurityManager, target);
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
