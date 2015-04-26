package no.rosbach.edu.compile;

/**
 * Created by mapster on 26.04.15.
 */
public class NonRecoverableError extends RuntimeException {

    public NonRecoverableError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
