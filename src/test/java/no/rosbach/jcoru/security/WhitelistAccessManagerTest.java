package no.rosbach.jcoru.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

public class WhitelistAccessManagerTest {

  private WhitelistAccessManager whitelist(String... entries) {
    return new WhitelistAccessManager(new HashSet<String>(Arrays.asList(entries)));
  }

  @Test
  public void shouldAnswerYesForLiteral() {
    assertTrue(whitelist("org.test.Klasse").hasAccess("org.test.Klasse"));
  }

  @Test
  public void shouldAnswerYesForLiteralNotSame() {
    assertTrue(whitelist("org.test.Klasse").hasAccess(new String("org.test.Klasse")));
  }

  @Test
  public void shouldAnswerYesForClassInPackage() {
    assertTrue(whitelist("org.test.*").hasAccess("org.test.Klasse"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void asteriksMustFollowADot() {
    whitelist("org.test*");
  }

  @Test(expected = IllegalArgumentException.class)
  public void asteriskOnlyIsIllegal() {
    whitelist("*");
  }

  @Test(expected = IllegalArgumentException.class)
  public void asteriskMustBeLastCharacter() {
    whitelist("org.*Test");
  }

  @Test
  public void shouldAnswerNoForClassInSubPackageOfWildcardEntry() {
    assertFalse(whitelist("org.*").hasAccess("org.test.Klasse"));
  }

  @Test
  public void shouldAnswerNoForClassNotWhitelisted() {
    assertFalse(whitelist("org.Klasse").hasAccess("Klasse"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotAllowEmptyEntry() {
    whitelist("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotAllowNull() {
    whitelist(new String[]{null});
  }

  /*

    Parsing tests.

   */

  private WhitelistAccessManager getWhitelist(final String name) {
    String resource = "whitelists/fixtures/" + name;
    try (InputStream fixtureStream = getClass().getClassLoader().getResourceAsStream(resource)) {
      ObjectMapper mapper = new ObjectMapper();
      return WhitelistAccessManager.fromJson(mapper.readValue(fixtureStream, ArrayNode.class));
    } catch (IOException e) {
      throw new Error("Failed to read whitelist fixture: " + resource, e);
    }
  }

  private void assertContainsJUnit(WhitelistAccessManager whitelist) {
    assertTrue(whitelist.hasAccess("org.unit.Test"));
    assertTrue(whitelist.hasAccess("org.unit.After"));
    assertTrue(whitelist.hasAccess("org.unit.Before"));
  }

  @Test
  public void acceptsBasicList() {
    WhitelistAccessManager whitelist = getWhitelist("basic_list.json");
    assertContainsJUnit(whitelist);
  }

  @Test
  public void acceptsMapWithList() {
    WhitelistAccessManager whitelist = getWhitelist("map_with_list.json");
    ;
    assertContainsJUnit(whitelist);
  }

  @Test
  public void acceptsMapWithMap() {
    WhitelistAccessManager whitelist = getWhitelist("map_with_map.json");
    ;
    Arrays.asList("java.util.List", "java.util.Date", "java.lang.String", "java.lang.System").stream()
        .forEach(clazz -> assertTrue(whitelist.hasAccess(clazz)));
  }

  @Test
  public void acceptsMapWithValue() {
    WhitelistAccessManager whitelist = getWhitelist("map_with_value.json");
    assertTrue(whitelist.hasAccess("org.junit.Test"));
  }

}
