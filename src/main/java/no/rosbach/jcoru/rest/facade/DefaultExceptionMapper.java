package no.rosbach.jcoru.rest.facade;

import no.rosbach.jcoru.rest.ErrorMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {
  private static final Logger LOGGER = LogManager.getLogger();

  @Override
  public Response toResponse(Throwable exception) {
    ErrorMessage msg = new ErrorMessage();
    setHttpStatus(exception, msg);
    msg.message = exception.getMessage();

    // Set developer message
    StringWriter stackTrace = new StringWriter();
    exception.printStackTrace(new PrintWriter(stackTrace));
    msg.developerMessage = stackTrace.toString();

    LOGGER.debug("Exception response.", exception);
    return Response.status(msg.status).entity(msg).type(MediaType.APPLICATION_JSON).build();
  }

  private void setHttpStatus(Throwable exception, ErrorMessage msg) {
    if (exception instanceof WebApplicationException) {
      msg.status = ((WebApplicationException) exception).getResponse().getStatus();
    } else {
      msg.status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
      LOGGER.error("Internal server error.", exception);
    }
  }
}
