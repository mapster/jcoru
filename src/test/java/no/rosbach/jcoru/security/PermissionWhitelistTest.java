package no.rosbach.jcoru.security;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.Test;

import java.security.Permission;
import java.util.Arrays;
import java.util.HashSet;

public class PermissionWhitelistTest extends WhitelistTestBase<Permission> {


  @Test(expected = IllegalArgumentException.class)
  public void elementsInRootListMustBeObjects() {
    whitelistFromJson("illegal_root_elements.json");
  }

  @Test(expected = IllegalArgumentException.class)
  public void doesNotAcceptUnsupportedPermissionTypes() {
    whitelistFromJson("unsupported_permission_type.json");
  }

  @Test
  public void acceptsRuntimePermissionWithSimpleList() {
    PermissionWhitelist whitelist = whitelistFromJson("runtime_permissions.json");
    assertTrue(whitelist.hasAccess(new RuntimePermission("accessMembers")));
    assertTrue(whitelist.hasAccess(new RuntimePermission("getClassLoader")));
  }

  @Test
  public void acceptsRuntimePermissionWithListOfApplicableElements() {
    PermissionWhitelist whitelist = whitelistFromJson("runtime_with_list.json");
    assertTrue(whitelist.hasAccess(new RuntimePermission("loadLibrary.junit-4.11.jar")));
    assertTrue(whitelist.hasAccess(new RuntimePermission("loadLibrary.commons-io-2.4.jar")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void doesNotAcceptSetWithIllegalWildcard() {
    new PermissionWhitelist(new HashSet<>(Arrays.asList(new RuntimePermission("*abc"))));
  }

  @Test
  public void acceptsUnknownPermissionTypeFromConstructor() {
    new PermissionWhitelist(
        new HashSet<>(
            Arrays.asList(
                new Permission("") {
                  @Override
                  public boolean implies(Permission permission) {
                    return false;
                  }

                  @Override
                  public boolean equals(Object obj) {
                    return false;
                  }

                  @Override
                  public int hashCode() {
                    return 0;
                  }

                  @Override
                  public String getActions() {
                    return null;
                  }
                })));
  }


  @Override
  protected ArrayNode getWhitelistJson(String name) {
    return super.getWhitelistJson("whitelists/fixtures/permission/" + name);
  }

  @Override
  protected Permission[] getTwoDistinctEntries() {
    return new Permission[]{new RuntimePermission("a"), new RuntimePermission("b")};
  }

  @Override
  protected AccessManager<Permission> createAccessManager(HashSet<Permission> entries) {
    return new PermissionWhitelist(entries);
  }

  @Override
  protected PermissionWhitelist whitelistFromJson(String filename) {
    return PermissionWhitelist.fromJson(getWhitelistJson(filename));
  }

}
