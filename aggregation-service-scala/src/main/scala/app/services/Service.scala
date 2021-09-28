package app.services

import app.ApplicationConfiguration
import app.kafka.KafkaConsumer
import app.services.api.HTTPService
import org.apache.log4j.BasicConfigurator

/**
 * The Main Class of the service Application.
 *
 * @author mbajramovic.
 */
object Service {

  /**
   * The Service Application Main Method.
   * @param args the program arguments
   */
  def main(args: Array[String]): Unit = {
    // Load application configuration.
    ApplicationConfiguration.init(args)

    // Configure Logger
    BasicConfigurator.configure()

    // Start HTTP service.
    HTTPService.start()

    // Start Consumer
    KafkaConsumer.start()
  }
}
