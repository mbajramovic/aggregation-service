package app.services.api.controllers

import app.models.VehicleStats
import app.models.enumerations.ErrorType
import app.models.transfer.{ErrorResponse, FailedVehicleStats, ResponseDTO}
import app.services.SampleService

/**
 * The Sample Controller.
 *
 * @author mbajramovic.
 */
object SampleController {

  def aggregateData(vehicleId: String): ResponseDTO = {
    val sample = SampleService.aggregateData(vehicleId)

    if (sample.isDefined) {
      sample.get
    } else {

      FailedVehicleStats(ErrorResponse(
        ErrorType.RESOURCE_NOT_FOUND.toString,
        s"No vehicle with id $vehicleId found",
        404
      ))
    }
  }
}
