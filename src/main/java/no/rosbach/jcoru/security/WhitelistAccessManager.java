package no.rosbach.jcoru.security;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WhitelistAccessManager extends AccessManager<String> {
  private final List<String> wildcardEntries;
  private final HashSet<String> entries;

  public WhitelistAccessManager(Set<String> entries) {
    super(entries);
    this.entries = new HashSet<>(entries);
    wildcardEntries = extractWildcardEntries(entries);
  }

  public static WhitelistAccessManager fromJson(ArrayNode jsonWhitelist) {
    return new WhitelistAccessManager(fromJson("", jsonWhitelist));
  }

  @Override
  protected boolean isIllegalEntry(String entry) {
    return hasIllegalWildcard(entry);
  }

  private List<String> extractWildcardEntries(Set<String> entries) {
    return entries.stream()
        .filter(e -> e.endsWith("*"))
        .map(e -> e.substring(0, e.length() - 1))
        .collect(toList());
  }

  @Override
  public AccessManager<String> extend(Set<String> additional) {
    return new WhitelistAccessManager(Sets.union(this.entries, additional));
  }

  @Override
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
