package app.models

import scala.collection.mutable

/**
 * Telemetry Sample class.
 *
 * @author mbajramovic.
 */
case class TelemetrySample(
  vehicleId: String,
  recordedAt: Long,
  signalValues: SignalValues
) {

  // Empty CTOR
  def this() = {
    this("", -1, new SignalValues())
  }

  def getVehicleId: String = this.vehicleId
  def getRecordedAt: Long = this.recordedAt
  def getSignalValues: SignalValues = this.signalValues

  def validate(): Boolean = {
    vehicleId.nonEmpty && recordedAt > 0
  }
}
