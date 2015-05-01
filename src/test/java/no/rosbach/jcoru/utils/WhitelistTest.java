package no.rosbach.jcoru.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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

}
