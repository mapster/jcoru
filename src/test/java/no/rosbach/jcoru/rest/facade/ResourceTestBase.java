package no.rosbach.jcoru.rest.facade;

import org.apache.commons.lang3.ArrayUtils;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;

public abstract class ResourceTestBase extends JerseyTest {
  private static final Class[] RESPONSE_MAPPERS = {DefaultExceptionMapper.class};

  protected abstract Class[] getFacadesToTest();

  protected abstract Invocation.Builder request();

  @Override
  final protected Application configure() {
    ResourceConfig resourceConfig = new ResourceConfig(ArrayUtils.addAll(getFacadesToTest(), RESPONSE_MAPPERS));
    resourceConfig.register(getCDIBindings());
    return resourceConfig;
  }

  protected abstract AbstractBinder getCDIBindings();
}
