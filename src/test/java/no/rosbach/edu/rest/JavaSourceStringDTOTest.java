package no.rosbach.edu.rest;

import org.junit.Test;

import no.rosbach.edu.compile.fixtures.Fixtures;
import no.rosbach.edu.filemanager.JavaSourceString;

import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;

/**
 * Created by mapster on 05.04.15.
 */
public class JavaSourceStringDTOTest {

    @Test
    public void verifyTransferIsInverse() {
        JavaSourceString source = getFixtureSource(Fixtures.TEST_CLASS);
        assertEquals(source, new JavaSourceStringDTO(source).transfer());
    }

    @Test
    public void verifyConstructorIsInverse() {
        JavaSourceStringDTO javaSourceStringDTO = new JavaSourceStringDTO("some.filename", "this is the sourcecode");
        assertEquals(javaSourceStringDTO, new JavaSourceStringDTO(javaSourceStringDTO.transfer()));
    }
}
