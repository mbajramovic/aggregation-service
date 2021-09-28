package app.models.transfer

/**
 * @author maid.
 */
final case class ErrorResponse(`type`: String = null, message: String = null, statusCode: Int = 500)
