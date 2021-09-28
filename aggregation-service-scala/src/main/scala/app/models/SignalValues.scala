package app.models

/**
 * @author maid.
 */
case class SignalValues(
  currentSpeed: Double,
  odometer: Double,
  drivingTime: Double,
  isCharging: Double
) {

  def this() = {
    this(
      currentSpeed = -1,
      odometer = -1,
      drivingTime = -1,
      isCharging = -1
    )
  }

  def getCurrentSpeed: Double = this.currentSpeed
  def getOdometer: Double = this.odometer
  def getDrivingTime: Double = this.drivingTime
  def getIsCharging: Double = this.isCharging
}
