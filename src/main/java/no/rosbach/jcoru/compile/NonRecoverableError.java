package no.rosbach.jcoru.compile;

/**
 * Exception indicating a non recoverable error. Used to wrap checked exceptions as runtime exceptions.
 */
public class NonRecoverableError extends RuntimeException {

  /**
   * Construct exception.
   *
   * @param msg   message.
   * @param cause cause exception.
   */
  public NonRecoverableError(String msg, Throwable cause) {
    super(msg, cause);
  }
}
