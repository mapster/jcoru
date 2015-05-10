package no.rosbach.jcoru.rest.facade;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.jboss.weld.environment.se.Weld;

import javax.ws.rs.core.Application;

public abstract class ResourceTestBase extends JerseyTest {
  private Weld weld;

  @Override
  public void setUp() throws Exception {
    weld = new Weld();
    weld.initialize();
    super.setUp();
  }

  @Override
  public void tearDown() throws Exception {
    weld.shutdown();
    super.tearDown();
  }

  @Override
  final protected Application configure() {
    return new ResourceConfig().packages("no.rosbach.jcoru.rest.facade");
  }

}
