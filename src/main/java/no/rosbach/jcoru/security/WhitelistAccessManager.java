package no.rosbach.jcoru.security;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

// TODO: Need a better scheme for permission access checking, see java.io.FilePermission
public class WhitelistAccessManager implements AccessManager {
  private final HashSet<String> entries;
  private final List<String> wildcardEntries;

  public WhitelistAccessManager(HashSet<String> entries) {
    entries.stream().filter(WhitelistAccessManager::isIllegalEntry)
        .map(illegal -> illegal == null ? "null" : illegal)
        .findFirst().ifPresent(
        illegal -> {
          throw new IllegalArgumentException("Illegal whitelist entry: " + illegal);
        });

    this.entries = entries;
    wildcardEntries = entries.stream()
        .filter(e -> e.endsWith("*"))
        .map(e -> e.substring(0, e.length() - 1))
        .collect(toList());
  }

  public WhitelistAccessManager(WhitelistAccessManager from, HashSet<String> additional) {
    this(new HashSet<>(Sets.union(from.entries, additional)));
  }

  public static boolean isIllegalEntry(String entry) {
    // null or empty string is not allowed
    if (entry == null || entry.isEmpty()) {
      return true;
    }
    // Wildcard only is not allowed
    if (entry.equals("*")) {
      return true;
    }
    // Wildcard must be last character
    if (entry.contains("*") && entry.indexOf('*') != entry.length() - 1) {
      return true;
    }
    // Wildcard must follow a period character '.'
    if (entry.endsWith("*") && entry.charAt(entry.length() - 2) != '.') {
      return true;
    }
    return false;
  }

  public static WhitelistAccessManager fromJson(ArrayNode jsonWhitelist) {
    return new WhitelistAccessManager(fromJson("", jsonWhitelist));
  }

  private static HashSet<String> fromJson(String parent, JsonNode current) {
    HashSet<String> entries = new HashSet<>();
    if (current.isTextual()) {
      // add parent if current is empty string.
      if (current.textValue().isEmpty()) {
        entries.add(parent);
      } else {
        entries.add(concatName(parent, current.textValue()));
      }
    } else if (current.isArray()) {
      for (JsonNode el : current) {
        entries.addAll(fromJson(parent, el));
      }
    } else if (current.isObject()) {
      for (Iterator<Entry<String, JsonNode>> entryIt = current.fields(); entryIt.hasNext(); ) {
        Entry<String, JsonNode> child = entryIt.next();
        entries.addAll(fromJson(concatName(parent, child.getKey()), child.getValue()));
      }
    } else {
      throw new IllegalArgumentException("Illegal whitelist syntax.");
    }
    return entries;
  }

  private static String concatName(String parent, String child) {
    if (parent == null || parent.isEmpty()) {
      return child;
    }
    return parent + "." + child;
  }

  public boolean hasAccess(String name) {
    if (entries.contains(name)) {
      return true;
    }
    for (String entry : wildcardEntries) {
      if (name.startsWith(entry) && !name.substring(entry.length()).contains(".")) {
        return true;
      }
    }
    return false;
  }
}
