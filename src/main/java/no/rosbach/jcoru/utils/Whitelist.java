package no.rosbach.jcoru.utils;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class Whitelist {
  private final HashSet<String> entries;
  private final List<String> wildcardEntries;

  public Whitelist(HashSet<String> entries) {
    entries.stream().filter(Whitelist::isIllegalEntry)
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

  public static Whitelist fromJson(ArrayNode jsonWhitelist) {
    return new Whitelist(fromJson("", jsonWhitelist));
  }

  private static HashSet<String> fromJson(String parent, JsonNode current) {
    HashSet<String> entries = new HashSet<>();
    if (current.isTextual()) {
      entries.add(concatName(parent, current.textValue()));
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

  public boolean contains(String subject) {
    if (entries.contains(subject)) {
      return true;
    }
    for (String entry : wildcardEntries) {
      if (subject.startsWith(entry) && !subject.substring(entry.length()).contains(".")) {
        return true;
      }
    }
    return false;
  }
}
