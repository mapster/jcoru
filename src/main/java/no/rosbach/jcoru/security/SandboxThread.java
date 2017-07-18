package no.rosbach.jcoru.security;

import java.util.UUID;

import static java.util.Objects.nonNull;

public class SandboxThread extends Thread {
  private final Object secret = UUID.randomUUID();
  private final StrictSecurityManager strictSecurityManager;
  private Runnable target;
  private Throwable thrownByTarget = null;
  private Throwable thrownBySandbox = null;

  public SandboxThread(StrictSecurityManager securityManager, Runnable target) {
    this.strictSecurityManager = securityManager;
    if (nonNull(this.strictSecurityManager)) {
      this.strictSecurityManager.enable(secret);
    }
    this.target = target;
  }

  @Override
  public void run() {
    if (target == null) {
      // nothing to do
      return;
    }

    try {
      SecurityManager old = System.getSecurityManager();
      System.setSecurityManager(strictSecurityManager);

      try {
        target.run();
      } catch (Throwable throwable) {
        this.thrownByTarget = throwable;
      }

      strictSecurityManager.disable(secret);
      System.setSecurityManager(old);
      target = null;
    } catch (Throwable throwable) {
      this.thrownBySandbox = throwable;
    }
  }

  public Throwable getThrownByTarget() {
    return thrownByTarget;
  }

  public Throwable getThrownBySandbox() {
    return thrownBySandbox;
  }
}
