package app.exceptions

/**
 * The Configuration Exception which indicates that there is a problem with application configuration.
 *
 * @author mbajramovic.
 */
class ConfigurationException(val message: String) extends RuntimeException(message)
