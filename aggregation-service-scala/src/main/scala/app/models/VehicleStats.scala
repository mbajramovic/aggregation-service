package app.models

import app.models.enumerations.VehicleStatus.VehicleStatus
import app.models.transfer.ResponseDTO

/**
 * The Vehicle Stats class.
 *
 * @author mbajramovic.
 */
final case class VehicleStats(
  averageSpeed: Double,
  maximumSpeed: Double,
  lastMessageTimestamp: Long,
  numberOfChargers: Int,
  vehicleStatus: VehicleStatus
) extends ResponseDTO
