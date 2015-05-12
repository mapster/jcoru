package no.rosbach.jcoru.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public abstract class AccessManager<T> {
  protected final HashSet<T> entries;

  public AccessManager(HashSet<T> entries) {
    validateEntries(entries);

    this.entries = entries;
  }

  public AccessManager(AccessManager<T> from, HashSet<T> additional) {
    this(new HashSet<T>(Sets.union(from.entries, additional)));
  }

  protected static String concatName(String parent, String child) {
    if (parent == null || parent.isEmpty()) {
      return child;
    }
    return parent + "." + child;
  }

  protected static HashSet<String> fromJson(String parent, JsonNode current) {
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
      for (Iterator<Map.Entry<String, JsonNode>> entryIt = current.fields(); entryIt.hasNext(); ) {
        Map.Entry<String, JsonNode> child = entryIt.next();
        entries.addAll(fromJson(concatName(parent, child.getKey()), child.getValue()));
      }
    } else {
      throw new IllegalArgumentException("Illegal whitelist syntax.");
    }
    return entries;
  }

  protected void validateEntries(HashSet<T> entries) {
    entries.stream().filter(this::isIllegalEntry)
        .map(illegal -> illegal == null ? "null" : illegal)
        .findFirst().ifPresent(
        illegal -> {
          throw new IllegalArgumentException("Illegal whitelist entry: " + illegal);
        });
  }

  protected boolean hasIllegalWildcard(String entry) {
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

  protected abstract boolean isIllegalEntry(T entry);

  public abstract boolean hasAccess(T name);

  public abstract AccessManager<T> extend(HashSet<T> additional);
}
