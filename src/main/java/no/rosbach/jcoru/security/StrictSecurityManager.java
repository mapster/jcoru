package no.rosbach.jcoru.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.security.util.SecurityConstants;

import java.io.FileDescriptor;
import java.io.FilePermission;
import java.net.InetAddress;
import java.security.Permission;
import java.security.SecurityPermission;
import java.util.HashSet;
import java.util.Set;

public class StrictSecurityManager extends SecurityManager {

  private static final Logger LOGGER = LogManager.getFormatterLogger(StrictSecurityManager.class);

  private String knownSecret;
  private Set<String> pkgWhitelist = new HashSet<>();
  private Set<Permission> permissionWhitelist = new HashSet<>();
  private Set<Permission> permissionsWhenDisabled = new HashSet<>();

  public StrictSecurityManager(String secret) {
    this.knownSecret = secret;

    readWhitelists();
  }

  private void readWhitelists() {
    // whitelisted packages
    String[] pkgs = {"java.lang", "java.math", "java.io", "java.util", "java.util.function",
//        "org.apache.commons.io", "org.apache.commons.io.input",
        "org.junit.runner", "org.junit.internal.runners", "org.junit.internal.runners.model", "org.junit.runner.notification",
    };
    for (String pkg : pkgs) {
      pkgWhitelist.add(pkg);
    }

    //whitelisted permissions
//    permissionWhitelist.add(new RuntimePermission("accessDeclaredMembers"));

    // permissions when disabled
    permissionsWhenDisabled.add(new RuntimePermission("setSecurityManager"));
  }

  public boolean disable(String givenSecret) {
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
      throw new StrictAccessControlException(validatingMethod + " denied access: " + perm, perm);
    }
  }

  @Override
  public void checkSecurityAccess(String target) {
    denyAccessIfActive("checkSecurityAccess", target);
    super.checkSecurityAccess(target);
  }

  @Override
  public void checkPermission(Permission perm) {
    if (permissionWhitelist.contains(perm)) {
      return;
    }
    denyAccessIfActive("checkPermission", perm);

    // skip checking with super if listed as allowed when disabled.
    // if not, check with super if this is allowed.
    if (!allowIfDisabled(perm)) {
      super.checkPermission(perm);
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
    denyAccessIfActive("checkAccess", SecurityConstants.MODIFY_THREAD_PERMISSION);
    super.checkAccess(t);
  }

  @Override
  public void checkAccess(ThreadGroup g) {
    denyAccessIfActive("checkAccess", SecurityConstants.MODIFY_THREAD_PERMISSION);
    super.checkAccess(g);
  }

  @Override
  public void checkExit(int status) {
    denyAccessIfActive("checkExit", new RuntimePermission("exitVM." + status));
    super.checkExit(status);
  }

  @Override
  public void checkExec(String cmd) {
    denyAccessIfActive("checkExec", new FilePermission(cmd, SecurityConstants.FILE_EXECUTE_ACTION));
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
    denyAccessIfActive("checkRead", new FilePermission(file, SecurityConstants.FILE_READ_ACTION));
    super.checkRead(file);
  }

  @Override
  public void checkRead(String file, Object context) {
    denyAccessIfActive("checkRead", new FilePermission(file, SecurityConstants.FILE_READ_ACTION));
    super.checkRead(file, context);
  }

  @Override
  public void checkWrite(FileDescriptor fd) {
    denyAccessIfActive("checkWrite", new RuntimePermission("writeFileDescriptor"));
    super.checkWrite(fd);
  }

  @Override
  public void checkWrite(String file) {
    denyAccessIfActive("checkWrite", new FilePermission(file, SecurityConstants.FILE_WRITE_ACTION));
    super.checkWrite(file);
  }

  @Override
  public void checkDelete(String file) {
    denyAccessIfActive("checkDelete", new FilePermission(file, SecurityConstants.FILE_DELETE_ACTION));
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
    if (!pkgWhitelist.contains(pkg)) {
      denyAccessIfActive("checkPackageAccess", new RuntimePermission("accessClassInPackage." + pkg));
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
    denyAccessIfActive("checkMemberAccess", SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
    super.checkMemberAccess(clazz, which);
  }
}
