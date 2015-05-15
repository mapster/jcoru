package no.rosbach.jcoru.security;

import no.rosbach.jcoru.provider.SecurityManagerWhitelist;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FilePermission;
import java.net.InetAddress;
import java.net.SocketPermission;
import java.security.Permission;
import java.security.SecurityPermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PropertyPermission;
import java.util.Set;

import javax.inject.Inject;

public class StrictSecurityManager extends SecurityManager {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final HashSet<Permission> REQUIRED_PERMISSIONS = new HashSet<>(
      Arrays.asList(
          new RuntimePermission("accessClassInPackage.org.apache.logging.log4j"),
          new RuntimePermission("accessClassInPackage.org.apache.logging.log4j.message"),
          // TODO: should be specified elsewhere
          new FilePermission("/var/lib/tomcat8/webapps/java-compile-service/WEB-INF/lib/hamcrest-core-1.1.jar", SecurityConstants.FILE_READ_ACTION)
      ));
  private static final Set<Permission> PERMISSIONS_WHEN_DISABLED = new HashSet<>(Arrays.asList(SecurityConstants.SET_SECURITY_MANAGER_PERMISSION));
  private static ThreadGroup rootGroup = getRootGroup();
  private final AccessManager<Permission> permissionWhitelist;
  private Object knownSecret;

  @Inject
  public StrictSecurityManager(@SecurityManagerWhitelist AccessManager<Permission> permissionWhitelist) {
    this.permissionWhitelist = permissionWhitelist.extend(REQUIRED_PERMISSIONS);
  }

  private static ThreadGroup getRootGroup() {
    ThreadGroup root = Thread.currentThread().getThreadGroup();
    while (root.getParent() != null) {
      root = root.getParent();
    }
    return root;
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

  private boolean isPermittedWhenDisabled(Permission perm) {
    return PERMISSIONS_WHEN_DISABLED.contains(perm);
  }

  private boolean isDisabled() {
    return this.knownSecret == null;
  }

  private boolean isWhitelisted(Permission perm) {
    return permissionWhitelist.hasAccess(perm);
  }

  /**
   * Denies access to the requested permission if the permission is not white listed. Certain permissions are only allowed when {@link #isDisabled()},
   * e.g. setSecurityManager.
   *
   * WARNING: The use of {@link #LOGGER} in this method may cause stack overflow issues if it is not permitted access to necessary permissions.
   *
   * @param perm the permission.
   */
  private void denyAccessUnlessWhitelisted_OR_disabledAndAllowed(Permission perm) {
    if (perm == null) {
      LOGGER.error("Attempt to check permission for null value.");
      throw new NullPointerException("Permission cannot be null.");
    }

    if (isWhitelisted(perm)) {
      LOGGER.debug("Access permitted by whitelist for: {}", perm);
      return;
    }

    if (isDisabled() && isPermittedWhenDisabled(perm)) {
      LOGGER.debug("Access permitted (security manager is disabled): {}", perm);
      return;
    }

    LOGGER.error("Access denied for: {}", perm);
    throw new StrictAccessControlException(String.format("Access denied for: %s", perm), perm);
  }

  @Override
  public void checkSecurityAccess(String target) {
    checkPermission(new SecurityPermission(target));
  }

  @Override
  public void checkPermission(Permission perm) {
    denyAccessUnlessWhitelisted_OR_disabledAndAllowed(perm);
  }

  @Override
  public void checkPermission(Permission perm, Object context) {
    LOGGER.error("Context of permission ({}) is ignored: {}", perm, context);
    checkPermission(perm);
  }

  @Override
  public void checkCreateClassLoader() {
    checkPermission(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
  }

  @Override
  public void checkAccess(Thread t) {
    if (t == null) {
      throw new NullPointerException("Thread cannot be null.");
    }

    if (t.getThreadGroup() == rootGroup) {
      LOGGER.error("Access denied to modify thread in root thread group.");
    }

    checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION);
  }

  @Override
  public void checkAccess(ThreadGroup g) {
    if (g == null) {
      throw new NullPointerException("Thread group cannot be null.");
    }

    if (g == rootGroup) {
      LOGGER.error("Access denied to modify root thread group.");
    }

    checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
  }

  @Override
  public void checkExit(int status) {
    checkPermission(new RuntimePermission("exitVM." + status));
  }

  @Override
  public void checkExec(String cmd) {
    File f = new File(cmd);

    if (f.isAbsolute()) {
      LOGGER.error("Access denied to execute file: {}", cmd);
    } else {
      LOGGER.error("Access denied to execute relative file {} with absolute path {}.", cmd, f.getAbsolutePath());
    }

    throw new StrictAccessControlException(String.format("Access denied to execute file: %s", cmd));
  }

  @Override
  public void checkLink(String lib) {
    LOGGER.error("Access denied to link library: {}", lib);
    throw new StrictAccessControlException(String.format("Access denied to link library: %s", lib));
  }

  @Override
  public void checkRead(FileDescriptor fd) {
    if (fd == null) {
      throw new NullPointerException("File descriptor cannot be null.");
    }
    LOGGER.debug("Access request to read file descriptor: {}", fd);
    checkPermission(new RuntimePermission("readFileDescriptor"));
  }

  @Override
  public void checkRead(String file) {
    checkPermission(new FilePermission(file, SecurityConstants.FILE_READ_ACTION));
  }

  @Override
  public void checkRead(String file, Object context) {
    checkPermission(new FilePermission(file, SecurityConstants.FILE_READ_ACTION), context);
  }

  @Override
  public void checkWrite(FileDescriptor fd) {
    if (fd == null) {
      throw new NullPointerException("File descriptor cannot be null.");
    }
    LOGGER.debug("Access request to write to file descriptor: {}", fd);
    checkPermission(new RuntimePermission("writeFileDescriptor"));
  }

  @Override
  public void checkWrite(String file) {
    checkPermission(new FilePermission(file, SecurityConstants.FILE_WRITE_ACTION));
  }

  @Override
  public void checkDelete(String file) {
    checkPermission(new FilePermission(file, SecurityConstants.FILE_DELETE_ACTION));
  }

  private SocketPermission getConnectPermission(String host, int port) {
    host = getHostForPermission(host, port);
    // If port is -1 then this is a resolve hostname permission request.
    if (port == -1) {
      return new SocketPermission(host, SecurityConstants.SOCKET_RESOLVE_ACTION);
    }
    return new SocketPermission(host, SecurityConstants.SOCKET_CONNECT_ACTION);
  }

  /**
   * Get a properly formatted host for permission checking. Ignores port if -1,
   *
   * @param host the host.
   * @param port the port.
   * @return formatted host.
   */
  private String getHostForPermission(String host, int port) {
    if (host == null) {
      throw new NullPointerException("Host cannot be null.");
    }
    // IPv6 check (if not already wrapped as a IPv6 literal, but is IPv6, then wrap).
    if (!host.startsWith("[") && host.indexOf(':') != -1) {
      host = "[" + host + "]";
    }
    return port == -1 ? host : (host + ":" + port);
  }

  @Override
  public void checkConnect(String host, int port) {
    checkPermission(getConnectPermission(host, port));
  }

  @Override
  public void checkConnect(String host, int port, Object context) {
    checkPermission(getConnectPermission(host, port), context);
  }

  @Override
  public void checkListen(int port) {
    checkPermission(new SocketPermission(getHostForPermission("localhost", port), SecurityConstants.SOCKET_LISTEN_ACTION));
  }

  @Override
  public void checkAccept(String host, int port) {
    checkPermission(new SocketPermission(getHostForPermission(host, port), SecurityConstants.SOCKET_ACCEPT_ACTION));
  }

  @Override
  public void checkMulticast(InetAddress maddr) {
    checkPermission(new SocketPermission(getHostForPermission(maddr.getHostAddress(), -1), SecurityConstants.SOCKET_CONNECT_ACCEPT_ACTION));
  }

  @Override
  public void checkMulticast(InetAddress maddr, byte ttl) {
    Permission perm = new SocketPermission(getHostForPermission(maddr.getHostAddress(), -1), SecurityConstants.SOCKET_CONNECT_ACCEPT_ACTION);
    LOGGER.error("Access permission request through deprecated method checkMulticast(InetAddress, byte): {}", perm);
    throw new StrictAccessControlException("Access denied by deprecated method checkMulticast(InetAddress, byte)", perm);
  }

  @Override
  public void checkPropertiesAccess() {
    checkPermission(new PropertyPermission("*", SecurityConstants.PROPERTY_RW_ACTION));
  }

  @Override
  public void checkPropertyAccess(String key) {
    checkPermission(new PropertyPermission(key, SecurityConstants.PROPERTY_RW_ACTION));
  }

  @Override
  public boolean checkTopLevelWindow(Object window) {
    LOGGER.error("Access permission request through deprecated method checkTopLevelWindow(Object).");
    throw new StrictAccessControlException("Access denied by deprecated method checkTopLevelWindow(Object)");
  }

  @Override
  public void checkPrintJobAccess() {
    checkPermission(new RuntimePermission("queuePrintJob"));
  }

  @Override
  public void checkSystemClipboardAccess() {
    LOGGER.error("Access permission request through deprecated method checkSystemClipboardAccess().");
    throw new StrictAccessControlException("Access denied by deprecated method checkSystemClipboardAccess()");
  }

  @Override
  public void checkAwtEventQueueAccess() {
    LOGGER.error("Access permission request through deprecated method checkAwtEventQueueAccess().");
    throw new StrictAccessControlException("Access denied by deprecated method checkAwtEventQueueAccess()");
  }

  @Override
  public void checkPackageAccess(String pkg) {
    if (pkg == null) {
      throw new NullPointerException("Package name cannot be null");
    }

    checkPermission(new RuntimePermission("accessClassInPackage." + pkg));
  }

  @Override
  public void checkPackageDefinition(String pkg) {
    if (pkg == null) {
      throw new NullPointerException("Package name cannot be null");
    }

    checkPermission(new RuntimePermission("defineClassInPackage." + pkg));
  }

  @Override
  public void checkSetFactory() {
    LOGGER.error("Access denied to set URL stream handler factory.");
    throw new StrictAccessControlException("Access denied to set URL stream handler factory.");
  }

  @Override
  public void checkMemberAccess(Class<?> clazz, int which) {
    LOGGER.error(
        "Access permission request through deprecated method checkMemberAccess(Class<?>, int) for: {} and access {}.",
        clazz.getName(),
        which);
    throw new StrictAccessControlException(
        String.format(
            "Access denied by deprecated method checkMemberAccess(Class<?>, int) for: %s and access %s.",
            clazz.getName(),
            which));
  }

  static class SecurityConstants {
    public static final String FILE_DELETE_ACTION = "delete";
    public static final String FILE_READ_ACTION = "read";
    public static final String FILE_WRITE_ACTION = "write";
    public static final String SOCKET_RESOLVE_ACTION = "resolve";
    public static final String SOCKET_CONNECT_ACTION = "connect";
    public static final String SOCKET_LISTEN_ACTION = "listen";
    public static final String SOCKET_ACCEPT_ACTION = "accept";
    public static final String SOCKET_CONNECT_ACCEPT_ACTION = "connect,accept";
    public static final String PROPERTY_RW_ACTION = "read,write";
    public static final RuntimePermission CREATE_CLASSLOADER_PERMISSION = new RuntimePermission("createClassLoader");
    public static final RuntimePermission MODIFY_THREAD_PERMISSION = new RuntimePermission("modifyThread");
    public static final RuntimePermission MODIFY_THREADGROUP_PERMISSION = new RuntimePermission("modifyThreadGroup");
    public static final RuntimePermission SET_SECURITY_MANAGER_PERMISSION = new RuntimePermission("setSecurityManager");
  }
}
