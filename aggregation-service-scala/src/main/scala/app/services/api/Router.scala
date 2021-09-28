package app.services.api

import akka.http.scaladsl.marshalling.{Marshal, Marshaller, ToResponseMarshallable}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import app.services.api.controllers.{HealthController, SampleController}
import app.utils.Json4sSupport._
import org.apache.log4j.Logger

import scala.concurrent.Future

/**
 * The Router Object. Handles HTTP request to Controller bindings.
 *
 * @author mbajramovic.
 */
object Router {

  def routes: Route = {
    pathPrefix("api" / "v1") {
      path("health") {
        get { ctx => logAndRun(ctx, HealthController.getClass, HealthController.checkHealth)}
      } ~
      pathPrefix("vehicle") {
        path(Segment) { vehicleId =>
          get { ctx => logAndRun(ctx, SampleController.getClass, SampleController.aggregateData(vehicleId))}
        }
      }
    }
  }

  /**
   * Logs HTTP requests.
   *
   * @param ctx the [[RequestContext]] request context
   * @param clazz the [[Class]] controller class
   * @param producedValue the result of request
   * @return the result
   */
  def logAndRun(ctx: RequestContext, clazz: Class[_], producedValue: ToResponseMarshallable): Future[RouteResult] = {
    val logger = Logger.getLogger(clazz)

    logger.info(s"Received HTTP ${ctx.request.method.value} request at ${ctx.request.uri}...")

    ctx.complete(producedValue)
  }
}
