package no.rosbach.jcoru.security;

import no.rosbach.jcoru.provider.SecurityManagerWhitelist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileDescriptor;
import java.io.FilePermission;
import java.net.InetAddress;
import java.security.Permission;
import java.security.SecurityPermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public class StrictSecurityManager extends SecurityManager {
  private static final HashSet<Permission> REQUIRED_PERMISSIONS = new HashSet<>(
      Arrays.asList(
          new RuntimePermission("accessClassInPackage.org.apache.logging.log4j"),
          new RuntimePermission("accessClassInPackage.org.apache.logging.log4j.message")));

  private static final Logger LOGGER = LogManager.getFormatterLogger(StrictSecurityManager.class);
  private final AccessManager<Permission> permissionWhitelist;
  private Object knownSecret;
  private Set<String> pkgWhitelist = new HashSet<>();
  private Set<Permission> permissionsWhenDisabled = new HashSet<>();

  @Inject
  public StrictSecurityManager(@SecurityManagerWhitelist AccessManager<Permission> permissionWhitelist) {
    this.permissionWhitelist = permissionWhitelist.extend(REQUIRED_PERMISSIONS);
    permissionsWhenDisabled.add(new RuntimePermission("setSecurityManager"));
    readWhitelists();
  }

  private void readWhitelists() {
    // whitelisted packages
    String[] pkgs = {"java.lang", "java.math", "java.io",
        "java.util", "java.util.function", "java.util.concurrent.atomic",
//        "org.apache.commons.io", "org.apache.commons.io.input",
        "org.junit.runner", "org.junit.runners", "org.junit.internal.runners", "org.junit.internal.runners.model", "org.junit.runner.notification",
    };
    for (String pkg : pkgs) {
      pkgWhitelist.add(pkg);
    }
  }

  public boolean enable(Object secret) {
    if (this.knownSecret == null) {
      this.knownSecret = secret;
    }
    return this.knownSecret == secret;
  }

  public boolean disable(Object givenSecret) {
    if (this.knownSecret == givenSecret) {
      this.knownSecret = null;
    }
    return this.knownSecret == null;
  }

  private boolean allowIfDisabled(Permission perm) {
    return permissionsWhenDisabled.contains(perm);
  }

  private void denyAccessIfActive(String validatingMethod) {
    denyAccessIfActive(validatingMethod, new RuntimePermission("no args"));
  }

  private void denyAccessIfActive(String validatingMethod, String target) {
    denyAccessIfActive(validatingMethod, new SecurityPermission(target));
  }

  private void denyAccessIfActive(String validatingMethod, Permission perm) {
    if (knownSecret != null) {
      // This is a potential danger sone! Causes stack overflow if manager is prevented from logging.
      LOGGER.error(validatingMethod + " denied access: " + perm);
      throw new StrictAccessControlException(validatingMethod + " denied access: " + perm, perm);
    }
  }

  // TODO: Need better pattern for checking whitelist.
  private boolean isWhitelisted(Permission perm) {
    // TODO: Need a info level log that permission was granted.
    return permissionWhitelist.hasAccess(perm);
  }

  @Override
  public void checkSecurityAccess(String target) {
    denyAccessIfActive("checkSecurityAccess", target);
    super.checkSecurityAccess(target);
  }

  @Override
  public void checkPermission(Permission perm) {
    if (!isWhitelisted(perm)) {
      denyAccessIfActive("checkPermission", perm);

      // skip checking with super if listed as allowed when disabled.
      // if not, check with super if this is allowed.
      if (!allowIfDisabled(perm)) {
        super.checkPermission(perm);
      }
    }
  }

  @Override
  public void checkPermission(Permission perm, Object context) {
    denyAccessIfActive("checkPermission", perm);
    super.checkPermission(perm, context);
  }

  @Override
  public void checkCreateClassLoader() {
    denyAccessIfActive("checkCreateClassLoader");
    super.checkCreateClassLoader();
  }

  @Override
  public void checkAccess(Thread t) {
    denyAccessIfActive("checkAccess", "modifyThread");
    super.checkAccess(t);
  }

  @Override
  public void checkAccess(ThreadGroup g) {
    denyAccessIfActive("checkAccess", "modifyThread");
    super.checkAccess(g);
  }

  @Override
  public void checkExit(int status) {
    denyAccessIfActive("checkExit", new RuntimePermission("exitVM." + status));
    super.checkExit(status);
  }

  @Override
  public void checkExec(String cmd) {
    denyAccessIfActive("checkExec", new FilePermission(cmd, "execute"));
    super.checkExec(cmd);
  }

  @Override
  public void checkLink(String lib) {
    denyAccessIfActive("checkLink", new RuntimePermission("loadLibrary." + lib));
    super.checkLink(lib);
  }

  @Override
  public void checkRead(FileDescriptor fd) {
    denyAccessIfActive("checkRead", new RuntimePermission("readFileDescriptor"));
    super.checkRead(fd);
  }

  @Override
  public void checkRead(String file) {
    denyAccessIfActive("checkRead", new FilePermission(file, "read"));
    super.checkRead(file);
  }

  @Override
  public void checkRead(String file, Object context) {
    denyAccessIfActive("checkRead", new FilePermission(file, "read"));
    super.checkRead(file, context);
  }

  @Override
  public void checkWrite(FileDescriptor fd) {
    denyAccessIfActive("checkWrite", new RuntimePermission("writeFileDescriptor"));
    super.checkWrite(fd);
  }

  @Override
  public void checkWrite(String file) {
    denyAccessIfActive("checkWrite", new FilePermission(file, "write"));
    super.checkWrite(file);
  }

  @Override
  public void checkDelete(String file) {
    denyAccessIfActive("checkDelete", new FilePermission(file, "delete"));
    super.checkDelete(file);
  }

  @Override
  public void checkConnect(String host, int port) {
    denyAccessIfActive("checkConnect");
    super.checkConnect(host, port);
  }

  @Override
  public void checkConnect(String host, int port, Object context) {
    denyAccessIfActive("checkConnect");
    super.checkConnect(host, port, context);
  }

  @Override
  public void checkListen(int port) {
    denyAccessIfActive("checkListen");
    super.checkListen(port);
  }

  @Override
  public void checkAccept(String host, int port) {
    denyAccessIfActive("checkAccept");
    super.checkAccept(host, port);
  }

  @Override
  public void checkMulticast(InetAddress maddr) {
    denyAccessIfActive("checkMulticast");
    super.checkMulticast(maddr);
  }

  @Override
  public void checkMulticast(InetAddress maddr, byte ttl) {
    denyAccessIfActive("checkMulticast");
    super.checkMulticast(maddr, ttl);
  }

  @Override
  public void checkPropertiesAccess() {
    denyAccessIfActive("checkPropertiesAccess");
    super.checkPropertiesAccess();
  }

  @Override
  public void checkPropertyAccess(String key) {
    denyAccessIfActive("checkPropertyAccess");
    super.checkPropertyAccess(key);
  }

  @Override
  public boolean checkTopLevelWindow(Object window) {
    denyAccessIfActive("checkTopLevelWindow");
    return super.checkTopLevelWindow(window);
  }

  @Override
  public void checkPrintJobAccess() {
    denyAccessIfActive("checkPrintJobAccess");
    super.checkPrintJobAccess();
  }

  @Override
  public void checkSystemClipboardAccess() {
    denyAccessIfActive("checkSystemClipboardAccess");
    super.checkSystemClipboardAccess();
  }

  @Override
  public void checkAwtEventQueueAccess() {
    denyAccessIfActive("checkAwtEventQueueAccess");
    super.checkAwtEventQueueAccess();
  }

  @Override
  public void checkPackageAccess(String pkg) {
    RuntimePermission perm = new RuntimePermission("accessClassInPackage." + pkg);
    if (!isWhitelisted(perm)) {
      denyAccessIfActive("checkPackageAccess", perm);
      super.checkPackageAccess(pkg);
    }
  }

  @Override
  public void checkPackageDefinition(String pkg) {
    denyAccessIfActive("checkPackageDefinition", new RuntimePermission("defineClassInPackage." + pkg));
    super.checkPackageDefinition(pkg);
  }

  @Override
  public void checkSetFactory() {
    denyAccessIfActive("checkSetFactory");
    super.checkSetFactory();
  }

  @Override
  public void checkMemberAccess(Class<?> clazz, int which) {
    denyAccessIfActive("checkMemberAccess", "accessDeclaredMembers");
    super.checkMemberAccess(clazz, which);
  }
}
