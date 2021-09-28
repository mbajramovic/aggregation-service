package app

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import javax.naming.ConfigurationException

/**
 * The Application Configuration Object. Serves as an interface to the actual application configuration file.
 *
 * @author mbajramovic.
 */
object ApplicationConfiguration {

  private[this] var configuration : Config = ConfigFactory.load()

  private val configurationFilePrefix = "--conf-file="

  def init(args: Array[String]) : Unit = {
    loadConfiguration(args, configurationFilePrefix)
  }

  private def loadConfiguration(launchParams: Array[String], propertyPrefix: String) : Unit = {
    val configurationFileParam = launchParams.find(_.startsWith(propertyPrefix))

    if (configurationFileParam.isDefined) {
      configuration = ConfigFactory.parseFile(new File(configurationFileParam.get.substring(propertyPrefix.length)))
    }
  }

  def http: Config = {
    if (configuration.hasPath("http")) {
      configuration.getConfig("http")
    } else {
      throw new ConfigurationException("HTTP Server configuration is required, but none was provided.");
    }
  }

  def kafka: Config = {
    if (configuration.hasPath("kafka.properties")) {
      configuration.getConfig("kafka.properties")
    } else {
      throw new ConfigurationException("Kafka Configuration is required, but none was provided.")
    }
  }

}
