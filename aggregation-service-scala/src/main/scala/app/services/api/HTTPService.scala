package app.services.api

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import app.ApplicationConfiguration

import scala.concurrent.ExecutionContext

/**
 * The HTTP Service Object.
 *
 * @author mbajramovic.
 */
object HTTPService {

  private val logger = org.apache.log4j.Logger.getLogger(this.getClass.getName)

  private val httpConfiguration = ApplicationConfiguration.http
  private val host = httpConfiguration.getString("host")
  private val port = httpConfiguration.getInt("port")

  implicit val actorSystem: ActorSystem = ActorSystem("aggregation-service-system")
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher


  def start() : Unit = {

    val ioThread = new Thread {
      override def run(): Unit = {
        Thread.currentThread.setName("HTTP-Listener-Thread")

        // Initialize the HTTP Service with all the routes
        val binding = Http().newServerAt(host, port).bind(Router.routes)

        logger.info("Web Service successfully started at http://" + host + ":" + port)

        while (!Thread.currentThread.isInterrupted) try {
          Thread.sleep(TimeUnit.MINUTES.toMillis(1))
        } catch {
          case _: Exception => Thread.currentThread.interrupt()
        }

        logger.info("Shutting down Web Service...")

        /**
         * Trigger server shutdown.
         *
         * Unbind from the port.
         * Close the spark session.
         * Terminate the Akka System.
         */
        binding.flatMap(_.unbind).onComplete(_ => actorSystem.terminate)
      }
    }

    ioThread.start()
  }

}
