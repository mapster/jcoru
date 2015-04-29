package no.rosbach.jcoru;

import sun.security.util.SecurityConstants;

import java.io.FileDescriptor;
import java.io.FilePermission;
import java.net.InetAddress;
import java.security.AccessControlException;
import java.security.Permission;
import java.security.SecurityPermission;

public class StrictSecurityManager extends SecurityManager {

  private String secret;

  public StrictSecurityManager(String secret) {
    this.secret = secret;
  }

  public void disable(String secret) {
    if (this.secret == secret) {
      secret = null;
    }
  }

  private void denyAccessIfActive() {
    denyAccessIfActive(new RuntimePermission("no args"));
  }

  private void denyAccessIfActive(String target) {
    denyAccessIfActive(new SecurityPermission(target));
  }

  private void denyAccessIfActive(Permission perm) {
    if (secret != null) {
      throw new AccessControlException("access denied " + perm, perm);
    }
  }

  @Override
  public void checkSecurityAccess(String target) {
    denyAccessIfActive(target);
    super.checkSecurityAccess(target);
  }

  @Override
  public void checkPermission(Permission perm) {
    denyAccessIfActive(perm);
    super.checkPermission(perm);
  }

  @Override
  public void checkPermission(Permission perm, Object context) {
    denyAccessIfActive(perm);
    super.checkPermission(perm, context);
  }

  @Override
  public void checkCreateClassLoader() {
    denyAccessIfActive();
    super.checkCreateClassLoader();
  }

  @Override
  public void checkAccess(Thread t) {
    denyAccessIfActive(SecurityConstants.MODIFY_THREAD_PERMISSION);
    super.checkAccess(t);
  }

  @Override
  public void checkAccess(ThreadGroup g) {
    denyAccessIfActive(SecurityConstants.MODIFY_THREAD_PERMISSION);
    super.checkAccess(g);
  }

  @Override
  public void checkExit(int status) {
    denyAccessIfActive(new RuntimePermission("exitVM." + status));
    super.checkExit(status);
  }

  @Override
  public void checkExec(String cmd) {
    denyAccessIfActive(new FilePermission(cmd, SecurityConstants.FILE_EXECUTE_ACTION));
    super.checkExec(cmd);
  }

  @Override
  public void checkLink(String lib) {
    denyAccessIfActive(new RuntimePermission("loadLibrary." + lib));
    super.checkLink(lib);
  }

  @Override
  public void checkRead(FileDescriptor fd) {
    denyAccessIfActive(new RuntimePermission("readFileDescriptor"));
    super.checkRead(fd);
  }

  @Override
  public void checkRead(String file) {
    denyAccessIfActive(new FilePermission(file, SecurityConstants.FILE_READ_ACTION));
    super.checkRead(file);
  }

  @Override
  public void checkRead(String file, Object context) {
    denyAccessIfActive(new FilePermission(file, SecurityConstants.FILE_READ_ACTION));
    super.checkRead(file, context);
  }

  @Override
  public void checkWrite(FileDescriptor fd) {
    denyAccessIfActive(new RuntimePermission("writeFileDescriptor"));
    super.checkWrite(fd);
  }

  @Override
  public void checkWrite(String file) {
    denyAccessIfActive(new FilePermission(file, SecurityConstants.FILE_WRITE_ACTION));
    super.checkWrite(file);
  }

  @Override
  public void checkDelete(String file) {
    denyAccessIfActive(new FilePermission(file, SecurityConstants.FILE_DELETE_ACTION));
    super.checkDelete(file);
  }

  @Override
  public void checkConnect(String host, int port) {
    denyAccessIfActive();
    super.checkConnect(host, port);
  }

  @Override
  public void checkConnect(String host, int port, Object context) {
    denyAccessIfActive();
    super.checkConnect(host, port, context);
  }

  @Override
  public void checkListen(int port) {
    denyAccessIfActive();
    super.checkListen(port);
  }

  @Override
  public void checkAccept(String host, int port) {
    denyAccessIfActive();
    super.checkAccept(host, port);
  }

  @Override
  public void checkMulticast(InetAddress maddr) {
    denyAccessIfActive();
    super.checkMulticast(maddr);
  }

  @Override
  public void checkMulticast(InetAddress maddr, byte ttl) {
    denyAccessIfActive();
    super.checkMulticast(maddr, ttl);
  }

  @Override
  public void checkPropertiesAccess() {
    denyAccessIfActive();
    super.checkPropertiesAccess();
  }

  @Override
  public void checkPropertyAccess(String key) {
    denyAccessIfActive();
    super.checkPropertyAccess(key);
  }

  @Override
  public boolean checkTopLevelWindow(Object window) {
    denyAccessIfActive();
    return super.checkTopLevelWindow(window);
  }

  @Override
  public void checkPrintJobAccess() {
    denyAccessIfActive();
    super.checkPrintJobAccess();
  }

  @Override
  public void checkSystemClipboardAccess() {
    denyAccessIfActive();
    super.checkSystemClipboardAccess();
  }

  @Override
  public void checkAwtEventQueueAccess() {
    denyAccessIfActive();
    super.checkAwtEventQueueAccess();
  }

  @Override
  public void checkPackageAccess(String pkg) {
    denyAccessIfActive(new RuntimePermission("accessClassInPackage." + pkg));
    super.checkPackageAccess(pkg);
  }

  @Override
  public void checkPackageDefinition(String pkg) {
    denyAccessIfActive(new RuntimePermission("defineClassInPackage." + pkg));
    super.checkPackageDefinition(pkg);
  }

  @Override
  public void checkSetFactory() {
    denyAccessIfActive();
    super.checkSetFactory();
  }

  @Override
  public void checkMemberAccess(Class<?> clazz, int which) {
    denyAccessIfActive(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
    super.checkMemberAccess(clazz, which);
  }
}
