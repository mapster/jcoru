package no.rosbach.jcoru;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.security.Permission;
import java.util.Arrays;

/**
 * Created by mapster on 28.04.15.
 */
public class StrictSecurityManagerTest {

  public static final String SECRET = "abc";
  private StrictSecurityManager sm;

  @Before
  public void prepare() {
    sm = new StrictSecurityManager(SECRET);
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
            }
            if (type == Permission.class) {
              return new RuntimePermission("a perm");
            } else if (type == String.class) {
              return "some";
            } else if (type == ThreadGroup.class) {
              return null;
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
      throw new Error("Argument type mismatch.", e);
    }

    assertEquals(
        String.format("%s: expected StrictSecuritManager to deny access, not pass along request.", method.getName()),
        StrictAccessControlException.class, actual.getClass());
  }
}
