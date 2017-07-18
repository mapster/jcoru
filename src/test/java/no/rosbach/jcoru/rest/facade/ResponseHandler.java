package no.rosbach.jcoru.rest.facade;

import no.rosbach.jcoru.rest.reports.BadRequestException;
import no.rosbach.jcoru.rest.reports.InternalServerError;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {
  public static void throwExceptionIfError(ResponseEntity response) {
    switch (response.getStatusCode()) {
      case BAD_REQUEST:
        throw new BadRequestException();
      case INTERNAL_SERVER_ERROR:
        throw new InternalServerError();
    }
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException("Unhandled error response: " + response.getStatusCodeValue());
    }
  }
}
