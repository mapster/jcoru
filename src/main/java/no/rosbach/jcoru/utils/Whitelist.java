package no.rosbach.jcoru.utils;

import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;

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
