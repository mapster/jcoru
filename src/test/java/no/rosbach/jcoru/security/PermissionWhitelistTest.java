package no.rosbach.jcoru.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.Test;

import java.security.Permission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.PropertyPermission;

public class PermissionWhitelistTest extends WhitelistTestBase<Permission> {

  public static final String LINE_SEPARATOR_KEY = "line.separator";
  public static final PropertyPermission READ_LINE_SEPARATOR_PERMISSION = new PropertyPermission(LINE_SEPARATOR_KEY, "read");
  public static final PropertyPermission WRITE_LINE_SEPARATOR_PERMISSION = new PropertyPermission(LINE_SEPARATOR_KEY, "write");

  /**
   * Parsing
   */

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
                new Permission("x") {
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

  @Test
  public void acceptsPropertyPermissionWithSingleAction() {
    PermissionWhitelist whitelist = whitelistFromJson("property_permission.json");
    assertTrue(whitelist.hasAccess(READ_LINE_SEPARATOR_PERMISSION));
  }

  @Test(expected = IllegalArgumentException.class)
  public void doesNotAcceptPropertyPermissionWithList() {
    whitelistFromJson("property_with_illegal_list.json");
  }

  @Test
  public void acceptsPropertyPermissionWithListOfActions() {
    PermissionWhitelist whitelist = whitelistFromJson("property_with_action_list.json");
    assertTrue(whitelist.hasAccess(READ_LINE_SEPARATOR_PERMISSION));
    assertTrue(whitelist.hasAccess(WRITE_LINE_SEPARATOR_PERMISSION));
  }

  /**
   * Functional
   */

  @Test
  public void doesNotPermitAccessWhenActionIsNotIncluded() {
    AccessManager<Permission> whitelist = whitelist(READ_LINE_SEPARATOR_PERMISSION);
    assertFalse(whitelist.hasAccess(WRITE_LINE_SEPARATOR_PERMISSION));
  }

  @Test
  public void doesNotPermitAccessWhenNoMatchingPropertyName() {
    assertFalse(whitelist(new PropertyPermission[0]).hasAccess(WRITE_LINE_SEPARATOR_PERMISSION));
  }

  @Test
  public void permitsAccessWhenActionIsIncluded() {
    AccessManager<Permission> whitelist = whitelist(new PropertyPermission(LINE_SEPARATOR_KEY, "read,write"));
    assertTrue(whitelist.hasAccess(READ_LINE_SEPARATOR_PERMISSION));
    assertTrue(whitelist.hasAccess(WRITE_LINE_SEPARATOR_PERMISSION));
  }

  @Test
  public void permitsAccessWithWildcardEntry() {
    assertTrue(whitelist(new PropertyPermission("line.*", "read")).hasAccess(new PropertyPermission(LINE_SEPARATOR_KEY, "read")));
  }

  @Test
  public void permitsAccessWithWildcardEntryWithMultipleActions() {
    assertTrue(whitelist(new PropertyPermission("line.*", "read,write")).hasAccess(new PropertyPermission(LINE_SEPARATOR_KEY, "read")));
  }

  @Test
  public void doesNotPermitsAccessWithWildcardEntryForNonCoveredAction() {
    assertFalse(whitelist(new PropertyPermission("line.*", "read")).hasAccess(new PropertyPermission(LINE_SEPARATOR_KEY, "write")));
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
