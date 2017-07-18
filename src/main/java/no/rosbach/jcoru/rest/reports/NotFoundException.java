package no.rosbach.jcoru.rest.reports;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Item not found exception.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such item")
@ResponseBody
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
