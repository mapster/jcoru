package no.rosbach.edu.compiler;

import no.rosbach.edu.compiler.fixtures.Fixtures;
import org.junit.Test;

import static no.rosbach.edu.compiler.fixtures.Fixtures.getFixtureSource;
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
