package no.rosbach.edu.compiler;

import no.rosbach.edu.compiler.fixtures.Fixtures;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

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
        JavaSourceStringDTO javaSource = new JavaSourceStringDTO(getFixtureSource(Fixtures.TEST_CLASS));
        JavaSourceStringDTO result = compileRequest(javaSource);
        assertEquals(javaSource, result);
    }

    private JavaSourceStringDTO compileRequest(JavaSourceStringDTO... javaSources) {
        List<JavaSourceStringDTO> entity = Arrays.asList(javaSources);
        GenericEntity<List<JavaSourceStringDTO>> listGenericEntity = new GenericEntity<List<JavaSourceStringDTO>>(entity) {};
        return target(CompilerResource.COMPILER_PATH).request().post(Entity.entity(listGenericEntity, MediaType.APPLICATION_JSON), JavaSourceStringDTO.class);
    }

}
