package no.rosbach.jcoru.security;

import java.security.AccessControlException;
import java.security.Permission;

public class StrictAccessControlException extends AccessControlException {

  public StrictAccessControlException(String s) {
    super(s);
  }

  public StrictAccessControlException(String s, Permission p) {
    super(s, p);
  }

}
