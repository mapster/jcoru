package no.rosbach.jcoru.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import no.rosbach.jcoru.provider.WhitelistProvider;

import org.junit.Before;
import org.junit.Test;

import java.io.FilePermission;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.security.Permission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by mapster on 28.04.15.
 */
public class StrictSecurityManagerTest {

  public static final String SECRET = "abc";
  private final WhitelistProvider whitelistProvider = new WhitelistProvider();
  private StrictSecurityManager sm;

  @Before
  public void prepare() {
    sm = new StrictSecurityManager(new PermissionWhitelist(new HashSet<>(Arrays.asList(new RuntimePermission("accessDeclaredMembers")))));
    sm.enable(SECRET);
  }

  @Test
  public void allCheckMethodsShouldThrowException() {
    Arrays.stream(sm.getClass().getMethods()).filter(m -> m.getName().startsWith("check")).forEach(this::assertThrowsSecurityException);
  }

  @Test
  public void canNotBeDisabledWithAnotherSecret() {
    assertFalse(sm.disable(new String("abc")));
  }

  @Test
  public void canBeDisabledWithTheSecret() {
    assertTrue(sm.disable(SECRET));
  }

  @Test
  public void canBeEnabled() {
    sm = new StrictSecurityManager(new PermissionWhitelist(new HashSet<>()));
    assertTrue(sm.enable(UUID.randomUUID()));
    try {
      sm.checkExit(1);
      fail("SecurityManager is not active.");
    } catch (StrictAccessControlException e) {
    }
  }

  @Test
  public void cannotBeEnabledWhenAlreadyEnabled() {
    assertFalse(sm.enable(UUID.randomUUID()));
  }

  @Test(expected = StrictAccessControlException.class)
  public void testThatSecurityExceptionAlsoIsThrownWhenNotInvokedThroughReflection() {
    sm.checkDelete("");
  }

  @Test
  public void whitelistIsEnabled() {
    sm.checkPermission(new RuntimePermission("accessDeclaredMembers"));
  }

  @Test(expected = StrictAccessControlException.class)
  public void whitelistChecksMultipleActions() {
    sm = new StrictSecurityManager(new PermissionWhitelist(new HashSet<>(Arrays.asList(new FilePermission("Test.java", "read")))));
    sm.enable(UUID.randomUUID());
    sm.checkPermission(new FilePermission("Test.java", "read,write"));
  }

  @Test
  public void whitelistAllowsMultipleActions() {
    sm = new StrictSecurityManager(new PermissionWhitelist(new HashSet<>(Arrays.asList(new FilePermission("Test.java", "read,write")))));
    sm.enable(UUID.randomUUID());
    sm.checkPermission(new FilePermission("Test.java", "read,write"));
  }

  @Test
  public void setSecurityManagerIsAllowedWhenDisabled() {
    sm.disable(SECRET);
    sm.checkPermission(new RuntimePermission("setSecurityManager"));
  }

  private void assertThrowsSecurityException(Method method) {
    Throwable actual = null;
    Object[] parameters = Arrays.stream(method.getParameterTypes()).map(
        type -> {
          try {
            if (type == Class.class) {
              return String.class;
            } else if (type == int.class) {
              return 0;
            } else if (type == byte.class) {
              return (byte) 0;
            } else if (type == Permission.class) {
              return new RuntimePermission("a perm");
            } else if (type == String.class) {
              return "some";
            } else if (type == ThreadGroup.class) {
              return new ThreadGroup("");
            } else if (type == InetAddress.class) {
              return InetAddress.getLocalHost();
            }
            return type.newInstance();
          } catch (Exception e) {
            throw new Error("Failed to create instance of parameter.", e);
          }
        }).toArray();

    try {
      method.invoke(sm, parameters);
//      fail(String.format("Expected AccessControlException for %s.", method));
    } catch (InvocationTargetException e) {
      Throwable target = e.getTargetException();
      if (SecurityException.class.isAssignableFrom(target.getClass())) {
        actual = target;
      } else {
        throw new Error(String.format("Invocation of %s failed.", method.getName()), e);
      }
    } catch (IllegalAccessException e) {
      throw new Error("Failed invoking method " + method.getName(), e);
    } catch (IllegalArgumentException e) {
      throw new Error("Argument type mismatch: " + method.getName(), e);
    }

    assertEquals(
        String.format("%s: expected StrictSecurityManager to deny access, not pass along request.", method.getName()),
        StrictAccessControlException.class, actual == null ? null : actual.getClass());
  }

}
