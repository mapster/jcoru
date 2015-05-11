package no.rosbach.jcoru.security;

import no.rosbach.jcoru.provider.WhitelistProvider;

import java.util.UUID;

public class SandboxThread extends Thread {
  private final WhitelistProvider whitelistProvider = new WhitelistProvider();
  private final Object secret = UUID.randomUUID();
  private final StrictSecurityManager strictSecurityManager;
  private Runnable target;
  private Throwable thrownByTarget = null;
  private Throwable thrownBySandbox = null;

  public SandboxThread(Runnable target) {
    this.strictSecurityManager = new StrictSecurityManager(whitelistProvider.getSecurityManagerWhitelist());
    this.strictSecurityManager.enable(secret);
    this.target = target;
  }

  SandboxThread(StrictSecurityManager securityManager, Runnable target) {
    this.strictSecurityManager = securityManager;
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
