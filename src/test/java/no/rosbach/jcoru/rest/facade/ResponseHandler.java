package no.rosbach.jcoru.rest.facade;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;

/**
 * Created by mapster on 24.04.15.
 */
public class ResponseHandler {
    public static void throwExceptionIfError(Response response) {
        switch (Response.Status.fromStatusCode(response.getStatus())) {
            case BAD_REQUEST:
                throw new BadRequestException();
            case INTERNAL_SERVER_ERROR:
                throw new InternalServerErrorException();
    }
        if (response.getStatus() >= Response.Status.BAD_REQUEST.getStatusCode()) {
            throw new RuntimeException("Unhandled error response: " + response.getStatus());
        }
    }
}
