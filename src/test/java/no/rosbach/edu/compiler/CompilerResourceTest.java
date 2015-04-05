package no.rosbach.edu.compiler;

import no.rosbach.edu.compiler.fixtures.Fixtures;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import static no.rosbach.edu.compiler.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;

/**
 * Created by mapster on 05.04.15.
 */
public class CompilerResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(CompilerResource.class);
    }

    @Test
    public void test() {
        JavaSourceString javaSource = getFixtureSource(Fixtures.TEST_CLASS);
        String result = compileRequest(javaSource);
        assertEquals(javaSource, result);
    }

    private String compileRequest(JavaSourceString javaSource) {
        return target(CompilerResource.COMPILER_PATH).request().post(Entity.entity(javaSource, MediaType.APPLICATION_JSON), String.class);
    }

}
