package no.rosbach.jcoru.security;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WhitelistTest extends SandboxTestBase {

  private void assertIsWhitelisted(Runnable expected) {
    runInSandbox(expected);
    assertNoErrors();
  }

  @Test
  public void ableToUseLists() {
    assertIsWhitelisted(
        () -> {
          List<String> list = new LinkedList<String>();
          list = new ArrayList<String>();
          list.add("abc");
          list.add("abc" + "123");
          String sum = new String();
          for (String s : list) {
            sum += s;
          }
        });
  }

  @Test
  public void ableToUsePrimitiveBoxes() {
    assertIsWhitelisted(
        () -> {
          Boolean b = true;
          Integer i = 1;
          Double d = 2.0;
          Long l = 1L;
          BigInteger bI = BigInteger.ONE;
          BigDecimal bD = BigDecimal.ONE;
        });
  }

  @Test
  public void ableToUseArrays() {
    assertIsWhitelisted(
        () -> {
          int[] ints = {1, 4, 7, 8};
          int sum = 0;
          for (int i = 0; i < ints.length; i++) {
            sum += ints[i];
          }
        });
  }

//  @Test @Ignore
//  public void xx() {
//    JUnitTestRunner testRunner = new JUnitTestRunner(Fixtures.getFixtureSources(Fixtures.TEST_SUBJECT_TEST, Fixtures.TEST_SUBJECT));
//    assertIsWhitelisted(testRunner);
//  }

}
