package no.rosbach.jcoru.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.security.Permission;
import java.util.*;

import static java.util.stream.Collectors.*;
import static no.rosbach.jcoru.utils.Stream.stream;

public class PermissionWhitelist extends AccessManager<Permission> {

  public static final String RUNTIME_PERMISSION = "java.lang.RuntimePermission";
  public static final String PROPERTY_PERMISSION = "java.util.PropertyPermission";
  private final HashMap<String, Permission> entries;
  private final List<Permission> wildcardEntries;

  public PermissionWhitelist(Set<Permission> permissions) {
    super(permissions);
    entries = new HashMap<>(permissions.stream().collect(toMap(p -> p.getName(), p -> p)));
    this.wildcardEntries = permissions.stream().filter(e -> e.getName().endsWith("*")).collect(toList());
  }

  public static PermissionWhitelist fromJson(ArrayNode permissionJsonList) {
    HashSet<Permission> permissions = new HashSet<>();

    // Iterate over root list.
    for (JsonNode el : permissionJsonList) {
      if (!el.isObject()) {
        throw new IllegalArgumentException("Illegal permission list syntax: Expected elements in root list to be JsonObject.");
      }

      // Iterate over each permission type entry in current object.
      for (Iterator<Map.Entry<String, JsonNode>> entryIt = el.fields(); entryIt.hasNext(); ) {
        Map.Entry<String, JsonNode> permission = entryIt.next();
        switch (permission.getKey()) {
          case RUNTIME_PERMISSION:
            permissions.addAll(runtimePermissionsFromJson(permission.getValue()));
            break;
          case PROPERTY_PERMISSION:
            permissions.addAll(propertyPermissionsFromJson(permission.getValue()));
            break;
          default:
            throw new IllegalArgumentException("Unsupported permission type: " + permission.getKey());
        }
      }
    }
    return new PermissionWhitelist(permissions);
  }

  private static Set<Permission> propertyPermissionsFromJson(JsonNode actionPermissions) {
    if (!actionPermissions.isObject()) {
      throw new IllegalArgumentException("Illegal PropertyPermission syntax: Expected direct child to by Object.");
    }

    HashSet<Permission> permissions = new HashSet<>();

    for (Iterator<Map.Entry<String, JsonNode>> permissionIterator = actionPermissions.fields(); permissionIterator.hasNext(); ) {
      Map.Entry<String, JsonNode> perm = permissionIterator.next();
      JsonNode actions = perm.getValue();
      if (actions.isTextual()) {
        permissions.add(new PropertyPermission(perm.getKey(), actions.asText()));
      } else if (actions.isArray()) {
        permissions.add(
            new PropertyPermission(
                perm.getKey(),
                stream(actions.elements()).map(a -> a.textValue()).reduce((a1, a2) -> String.format("%s,%s", a1, a2)).get()));
      }
    }

    return permissions;
  }

  private static Set<RuntimePermission> runtimePermissionsFromJson(JsonNode runtimePermissions) {
    return fromJson(null, runtimePermissions).stream().map(p -> new RuntimePermission(p)).collect(toSet());
  }

  @Override
  protected boolean isIllegalEntry(Permission entry) {
    return hasIllegalWildcard(entry.getName());
  }

  @Override
  public boolean hasAccess(final Permission permission) {
    Permission whitelisted = entries.get(permission.getName());
    if (whitelisted != null) {
      return whitelisted.implies(permission);
    }

    for (Permission whitelistedWildcard : wildcardEntries) {
      if (whitelistedWildcard.implies(permission)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public AccessManager<Permission> extend(Set<Permission> additional) {
    HashSet<Permission> union = new HashSet<>(entries.values());
    union.addAll(additional);
    return new PermissionWhitelist(union);
  }
}
