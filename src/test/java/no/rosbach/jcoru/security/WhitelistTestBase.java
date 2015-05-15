package no.rosbach.jcoru.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

public abstract class WhitelistTestBase<T> {

  protected AccessManager<T> whitelist(T... entries) {
    return createAccessManager(new HashSet<T>(Arrays.asList(entries)));
  }

  protected ArrayNode getWhitelistJson(final String resource) {
    try (InputStream fixtureStream = getClass().getClassLoader().getResourceAsStream(resource)) {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(fixtureStream, ArrayNode.class);
    } catch (IOException e) {
      throw new Error("Failed to read whitelist fixture: " + resource, e);
    }
  }

  protected abstract AccessManager<T> whitelistFromJson(String json);

  protected abstract T[] getTwoDistinctEntries();

  protected abstract AccessManager<T> createAccessManager(HashSet<T> entries);

  @Test
  public void extendReturnsNewInstance() {
    AccessManager<T> whitelist = createAccessManager(new HashSet<>());

    AccessManager<T> extended = whitelist.extend(new HashSet<>());
    assertNotSame(whitelist, extended);
  }

  @Test
  public void extendReturnsUnionOfWhitelists() {
    T[] entries = getTwoDistinctEntries();
    AccessManager<T> whitelist = createAccessManager(new HashSet<>(Arrays.asList(entries[0])));

    AccessManager<T> extended = whitelist.extend(new HashSet<>(Arrays.asList(entries[1])));
    assertEquals(2, extended.entries.size());
    assertTrue(extended.hasAccess(entries[0]));
    assertTrue(extended.hasAccess(entries[1]));
  }
}
