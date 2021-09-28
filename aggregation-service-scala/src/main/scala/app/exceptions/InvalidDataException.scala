package app.exceptions

/**
 * Invalid Data Exception which indicates that there is a problem with provided data.
 *
 * @author mbajramovic.
 */
case class InvalidDataException(val message: String) extends RuntimeException(message)
