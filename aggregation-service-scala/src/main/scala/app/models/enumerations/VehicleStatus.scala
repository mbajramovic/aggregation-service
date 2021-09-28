package app.models.enumerations

/**
 * The Vehicle Status Object.
 *
 * @author mbajramovic.
 */
object VehicleStatus extends Enumeration {

  type VehicleStatus = Value

  val DRIVING,
  CHARGING,
  PARKED,
  UNKNOWN = Value
}
