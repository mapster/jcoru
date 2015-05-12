package no.rosbach.jcoru.security;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.List;

public class WhitelistAccessManager extends AccessManager<String> {
  private final List<String> wildcardEntries;

  public WhitelistAccessManager(HashSet<String> entries) {
    super(entries);
    wildcardEntries = extractWildcardEntries(entries);
  }

  public WhitelistAccessManager(AccessManager<String> from, HashSet<String> additional) {
    this(new HashSet<String>(Sets.union(from.entries, additional)));
  }

  public static WhitelistAccessManager fromJson(ArrayNode jsonWhitelist) {
    return new WhitelistAccessManager(fromJson("", jsonWhitelist));
  }

  @Override
  protected boolean isIllegalEntry(String entry) {
    return hasIllegalWildcard(entry);
  }

  private List<String> extractWildcardEntries(HashSet<String> entries) {
    return entries.stream()
        .filter(e -> e.endsWith("*"))
        .map(e -> e.substring(0, e.length() - 1))
        .collect(toList());
  }

  @Override
  public AccessManager<String> extend(AccessManager<String> from, HashSet<String> additional) {
    return new WhitelistAccessManager(from, additional);
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
