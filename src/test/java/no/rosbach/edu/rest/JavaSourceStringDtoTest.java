package no.rosbach.edu.rest;

import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;

import no.rosbach.edu.compile.fixtures.Fixtures;
import no.rosbach.edu.filemanager.JavaSourceString;

import org.junit.Test;

/**
 * Created by mapster on 05.04.15.
 */
public class JavaSourceStringDtoTest {

  @Test
  public void verifyTransferIsInverse() {
    JavaSourceString source = getFixtureSource(Fixtures.TEST_CLASS);
    assertEquals(source, new JavaSourceStringDto(source).transfer());
  }

  @Test
  public void verifyConstructorIsInverse() {
    JavaSourceStringDto javaSourceStringDto = new JavaSourceStringDto("some.filename", "this is the sourcecode");
    assertEquals(javaSourceStringDto, new JavaSourceStringDto(javaSourceStringDto.transfer()));
  }
}
