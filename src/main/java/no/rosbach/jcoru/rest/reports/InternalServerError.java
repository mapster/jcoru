package no.rosbach.jcoru.rest.reports;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Internal server error.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal server error")
@ResponseBody
public class InternalServerError extends RuntimeException {
    public InternalServerError(String message) {
        super(message);
    }

    public InternalServerError(String message, Throwable t) {
        super(message, t);
    }

    public InternalServerError() {
        super();
    }
}
