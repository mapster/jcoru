package no.rosbach.jcoru.security;

import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.security.Permission;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PermissionWhitelist extends AccessManager<Permission> {

  public static final String RUNTIME_PERMISSION = "java.lang.RuntimePermission";

  public PermissionWhitelist(HashSet<Permission> permissions) {
    super(permissions);
  }

  public PermissionWhitelist(AccessManager<Permission> from, HashSet<Permission> additional) {
    super(from, additional);
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
          default:
            throw new IllegalArgumentException("Unsupported permission type: " + permission.getKey());
        }
      }
    }
    return new PermissionWhitelist(permissions);
  }

  private static Set<RuntimePermission> runtimePermissionsFromJson(JsonNode runtimePermissions) {
    return fromJson(null, runtimePermissions).stream().map(p -> new RuntimePermission(p)).collect(toSet());
  }

  @Override
  protected boolean isIllegalEntry(Permission entry) {
    switch (entry.getClass().getName()) {
      case RUNTIME_PERMISSION:
        return hasIllegalWildcard(entry.getName());
    }
    return false;
  }

  @Override
  public boolean hasAccess(Permission name) {
//    String[] permissions;
//
//    String accessName = String.format("%s.%s", perm.getClass().getName(), perm.getName());
//    String actions = perm.getActions();
//    if (actions != null && actions.length() > 0) {
//      permissions = stream(actions.split(",")).map(action -> accessName + "." + action).toArray(String[]::new);
//    } else {
//      permissions = new String[]{accessName};
//    }

    return entries.contains(name);
  }

  @Override
  public AccessManager<Permission> extend(HashSet<Permission> additional) {
    return new PermissionWhitelist(this, additional);
  }
}
