package no.rosbach.edu.rest.facade;

import no.rosbach.edu.compile.fixtures.Fixtures;
import no.rosbach.edu.rest.JavaSourceStringDTO;
import no.rosbach.edu.rest.reports.CompilationReport;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureInterfaceSource;
import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by mapster on 05.04.15.
 */
public class CompilerResourceTest extends JerseyTest {

    public static final JavaSourceStringDTO TEST_CLASS_SOURCE = new JavaSourceStringDTO(getFixtureSource(Fixtures.TEST_CLASS));
    public static final JavaSourceStringDTO TEST_CLASS_I_SOURCE = new JavaSourceStringDTO(getFixtureInterfaceSource(Fixtures.TEST_CLASS));

    @Override
    protected Application configure() {
        return new ResourceConfig(CompilerResource.class);
    }

    @Test
    public void compilerReturnsSuccessReport() {
        CompilationReport result = compileRequest(TEST_CLASS_SOURCE, TEST_CLASS_I_SOURCE);
        assertTrue(result.isSuccess());
    }

    @Test
    public void compilerReturnsFailedReport() {
        CompilationReport result = compileRequest(TEST_CLASS_SOURCE);
        assertFalse(result.isSuccess());
    }

    @Test(expected = BadRequestException.class)
    public void returnsBadRequestForJavaFileMissingSourceString() {
        compileRequest(new JavaSourceStringDTO("TestClass.java", null));
    }

    @Test(expected = BadRequestException.class)
    public void returnsBadRequestForJavaFileMissingFileName() {
        compileRequest(new JavaSourceStringDTO("", "This is some java source"));
    }

    @Test(expected = BadRequestException.class)
    public void returnsBadRequestForNullPost() {
        ResponseHandler.throwExceptionIfError(target(CompilerResource.COMPILER_PATH).request().post(null));
    }

    private CompilationReport compileRequest(JavaSourceStringDTO... javaSources) {
        List<JavaSourceStringDTO> entity = Arrays.asList(javaSources);
        GenericEntity<List<JavaSourceStringDTO>> listGenericEntity = new GenericEntity<List<JavaSourceStringDTO>>(entity) {};

        Response response = target(CompilerResource.COMPILER_PATH).request().post(Entity.entity(listGenericEntity, MediaType.APPLICATION_JSON));
        ResponseHandler.throwExceptionIfError(response);

        return response.readEntity(CompilationReport.class);
    }

}
