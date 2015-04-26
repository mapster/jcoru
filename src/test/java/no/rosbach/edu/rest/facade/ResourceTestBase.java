package no.rosbach.edu.rest.facade;

import no.rosbach.edu.rest.ErrorMessage;
import org.apache.commons.lang3.ArrayUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by mapster on 26.04.15.
 */
public abstract class ResourceTestBase extends JerseyTest {

    private static final Class[] RESPONSE_MAPPERS = {DefaultExceptionMapper.class};

    protected abstract Class[] getFacadesToTest();

    @Override
    final protected Application configure() {
        return new ResourceConfig(ArrayUtils.addAll(getFacadesToTest(), RESPONSE_MAPPERS));
    }

    protected Response stringRequest(String s) {
        Entity entity = null;
        if(s != null) {
            entity = Entity.entity(s, MediaType.APPLICATION_JSON);
        }
        return target(CompilerResource.COMPILER_PATH).request().post(entity);
    }


    protected void expectMessageAndException(Response response, Class<? extends WebApplicationException> expectedException) {
        try {
            ErrorMessage msg = response.readEntity(ErrorMessage.class);
            assertEquals(response.getStatus(), msg.status);
        } catch (Exception e) {
            fail();
        }
        try {
            ResponseHandler.throwExceptionIfError(response);
        } catch (WebApplicationException thrown) {
            assertEquals(expectedException, thrown.getClass());
        }
    }
}
