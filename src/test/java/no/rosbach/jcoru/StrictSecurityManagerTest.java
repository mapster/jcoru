package no.rosbach.jcoru;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.Arrays;

/**
 * Created by mapster on 28.04.15.
 */
public class StrictSecurityManagerTest {

  private StrictSecurityManager sm;

  @Before
  public void prepare() {
    sm = new StrictSecurityManager("abc");
  }

  @Test
  public void allCheckMethodsShouldThrowException() {
    Arrays.stream(sm.getClass().getMethods()).filter(m -> m.getName().startsWith("check")).forEach(this::assertThrowsSecurityException);
  }

  private void assertThrowsSecurityException(Method method) {
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
      fail(String.format("Expected AccessControlException for %s.", method));
    } catch (InvocationTargetException e) {
      assertTrue(String.format("Invocation of %s failed.", method.getName()), e.getTargetException() instanceof AccessControlException);
    } catch (IllegalAccessException e) {
      throw new Error("Failed invoking method " + method.getName(), e);
    } catch (IllegalArgumentException e) {
      throw new Error("Argument type mismatch.", e);
    }
  }
}
