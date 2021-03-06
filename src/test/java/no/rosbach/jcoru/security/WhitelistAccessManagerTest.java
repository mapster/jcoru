package no.rosbach.jcoru.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class WhitelistAccessManagerTest extends WhitelistTestBase<String> {

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

  @Test
  public void doesNotPermitParentOfWildcardEntry() {
    assertFalse(whitelist("java.lang.*").hasAccess("java.lang"));
  }

  /*

    Parsing tests.

   */

  @Test
  public void acceptsBasicList() {
    assertContainsJUnit(whitelistFromJson("basic_list.json"));
  }

  @Test
  public void acceptsMapWithList() {
    WhitelistAccessManager whitelist = whitelistFromJson("map_with_list.json");
    assertContainsJUnit(whitelist);
  }

  @Test
  public void acceptsMapWithMap() {
    WhitelistAccessManager whitelist = whitelistFromJson("map_with_map.json");
    Arrays.asList("java.util.List", "java.util.Date", "java.lang.String", "java.lang.System").stream()
        .forEach(clazz -> assertTrue(whitelist.hasAccess(clazz)));
  }

  @Test
  public void acceptsMapWithValue() {
    WhitelistAccessManager whitelist = whitelistFromJson("map_with_value.json");
    assertTrue(whitelist.hasAccess("org.junit.Test"));
  }

  @Test
  public void acceptsListWithEmptyStringAsSelf() {
    WhitelistAccessManager whitelist = whitelistFromJson("list_with_emptystring.json");
    assertTrue(whitelist.hasAccess("org.junit"));
  }

  @Override
  protected WhitelistAccessManager whitelistFromJson(String json) {
    return WhitelistAccessManager.fromJson(getWhitelistJson(json));
  }

  @Override
  protected ArrayNode getWhitelistJson(String name) {
    return super.getWhitelistJson("whitelists/fixtures/simple/" + name);
  }

  @Override
  protected String[] getTwoDistinctEntries() {
    return new String[]{"A", "B"};
  }

  @Override
  protected AccessManager<String> createAccessManager(HashSet<String> entries) {
    return new WhitelistAccessManager(entries);
  }

  private void assertContainsJUnit(WhitelistAccessManager whitelist) {
    assertTrue(whitelist.hasAccess("org.unit.Test"));
    assertTrue(whitelist.hasAccess("org.unit.After"));
    assertTrue(whitelist.hasAccess("org.unit.Before"));
  }
}
