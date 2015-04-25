package no.rosbach.edu.rest.facade;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

/**
 * Created by mapster on 24.04.15.
 */
public class ResponseHandler {
    public static void throwExceptionIfError(Response response) {
        switch (Response.Status.fromStatusCode(response.getStatus())) {
            case BAD_REQUEST:
                throw new BadRequestException(response.getEntity().toString());
        }
    }
}
