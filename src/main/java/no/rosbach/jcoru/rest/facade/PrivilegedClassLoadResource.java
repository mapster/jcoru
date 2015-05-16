package no.rosbach.jcoru.rest.facade;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/loadclass")
public class PrivilegedClassLoadResource {

  @GET
  public Response loadClass(@QueryParam("name") String name) {
    try {
      Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException("Could not load class.", e);
    }
    return Response.noContent().build();
  }
}
