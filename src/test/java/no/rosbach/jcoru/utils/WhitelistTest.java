package no.rosbach.jcoru.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;

public class WhitelistTest {

  private Whitelist whitelist(String... entries) {
    return new Whitelist(new HashSet<String>(Arrays.asList(entries)));
  }

  @Test
  public void shouldAnswerYesForLiteral() {
    assertTrue(whitelist("org.test.Klasse").contains("org.test.Klasse"));
  }

  @Test
  public void shouldAnswerYesForLiteralNotSame() {
    assertTrue(whitelist("org.test.Klasse").contains(new String("org.test.Klasse")));
  }

  @Test
  public void shouldAnswerYesForClassInPackage() {
    assertTrue(whitelist("org.test.*").contains("org.test.Klasse"));
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
    assertFalse(whitelist("org.*").contains("org.test.Klasse"));
  }

  @Test
  public void shouldAnswerNoForClassNotWhitelisted() {
    assertFalse(whitelist("org.Klasse").contains("Klasse"));
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

  private Whitelist getWhitelist(final String name) {
    String resource = "whitelists/fixtures/" + name;
    try (InputStream fixtureStream = getClass().getClassLoader().getResourceAsStream(resource)) {
      ObjectMapper mapper = new ObjectMapper();
      return Whitelist.fromJson(mapper.readValue(fixtureStream, ArrayNode.class));
    } catch (IOException e) {
      throw new Error("Failed to read whitelist fixture: " + resource, e);
    }
  }

  private void assertContainsJUnit(Whitelist whitelist) {
    assertTrue(whitelist.contains("org.unit.Test"));
    assertTrue(whitelist.contains("org.unit.After"));
    assertTrue(whitelist.contains("org.unit.Before"));
  }

  @Test
  public void acceptsBasicList() {
    Whitelist whitelist = getWhitelist("basic_list.json");
    assertContainsJUnit(whitelist);
  }

  @Test
  public void acceptsMapWithList() {
    Whitelist whitelist = getWhitelist("map_with_list.json");
    ;
    assertContainsJUnit(whitelist);
  }

  @Test
  public void acceptsMapWithMap() {
    Whitelist whitelist = getWhitelist("map_with_map.json");
    ;
    Arrays.asList("java.util.List", "java.util.Date", "java.lang.String", "java.lang.System").stream()
        .forEach(clazz -> assertTrue(whitelist.contains(clazz)));
  }

  @Test
  public void acceptsMapWithValue() {
    Whitelist whitelist = getWhitelist("map_with_value.json");
    assertTrue(whitelist.contains("org.junit.Test"));
  }

}
