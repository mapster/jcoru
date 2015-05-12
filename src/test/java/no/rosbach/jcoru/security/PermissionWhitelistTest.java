package no.rosbach.jcoru.security;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.Test;

public class PermissionWhitelistTest extends WhitelistTestBase {

  private PermissionWhitelist whitelist;

  @Override
  protected ArrayNode getWhitelistJson(String name) {
    return super.getWhitelistJson("whitelists/fixtures/permission/" + name);
  }

  @Test(expected = IllegalArgumentException.class)
  public void elementsInRootListMustBeObjects() {
    createWhitelist("illegal_root_elements.json");
  }

  @Test(expected = IllegalArgumentException.class)
  public void doesNotAcceptUnsupportedPermissionTypes() {
    createWhitelist("unsupported_permission_type.json");
  }

  @Test
  public void acceptsRuntimePermissionWithSimpleList() {
    createWhitelist("runtime_permissions.json");
    assertHasAccess("accessMembers");
    assertHasAccess("getClassLoader");
  }

  @Test
  public void acceptsRuntimePermissionWithListOfApplicableElements() {
    createWhitelist("runtime_with_list.json");
    assertHasAccess("loadLibrary.junit-4.11.jar");
    assertHasAccess("loadLibrary.commons-io-2.4.jar");
  }

  private void createWhitelist(String filename) {
    whitelist = PermissionWhitelist.fromJson(getWhitelistJson(filename));
  }

  private void assertHasAccess(String permission) {
    assertTrue(whitelist.hasAccess(new RuntimePermission(permission)));
  }
}
