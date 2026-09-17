package se.lnu.savechecker;

/**
 * Thrown when save file data cannot be parsed because it has the wrong
 * size, an invalid checksum, or otherwise does not match the expected
 * Generation I save file format.
 */
public final class InvalidSaveFileException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an exception describing why the save file is invalid.
   *
   * @param message describes why the save file is invalid
   */
  public InvalidSaveFileException(String message) {
    super(message);
  }

  /**
   * Creates an exception describing why the save file is invalid, wrapping
   * the underlying cause of the failure.
   *
   * @param message describes why the save file is invalid
   * @param cause the underlying exception that caused this failure
   */
  public InvalidSaveFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
