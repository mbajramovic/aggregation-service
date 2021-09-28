package app.services

import app.exceptions.InvalidDataException
import app.models.enumerations.VehicleStatus
import app.models.{SignalValues, TelemetrySample}
import org.scalatest.{BeforeAndAfterEach, FunSuite}

/**
 * Covers functionality of Sample Service object.
 *
 * @author mbajramovic.
 */
class SampleServiceTest extends FunSuite with BeforeAndAfterEach {

  override def beforeEach() {
    SampleService.removeAll()
  }

  test("Tests if `add` method works as expected.") {
    val sample = createSample("1")
    val added = SampleService.add(sample)
    assert(added != null)
    assert(sample.getVehicleId.equals(added.getVehicleId))
  }

  test("Tests if `add` method throws exception in case of invalid sample.") {
    val sample = createSample("")
    assertThrows[InvalidDataException] {
      SampleService.add(sample)
    }
  }

  test("Tests if `aggregateData` method works as expected for average speed.") {
    val carAFirstSample = createSample("1")
    val carASecondSample = createSample("1", 1613382090490L, 55D, 1200D)
    val catBFirstSample = createSample("2")

    SampleService.add(carAFirstSample)
    SampleService.add(carASecondSample)
    SampleService.add(catBFirstSample)

    val result = SampleService.aggregateData("1")

    assert(result.isDefined)

    val stats = result.get

    // Last value from odometer is 1200km, while the last value for drivingTime is 20h.
    assert(stats.averageSpeed == 60.0)
  }

  test("Tests if `aggregateData` method works as expected for maximum speed.") {
    val carAFirstSample = createSample("1")
    val carASecondSample = createSample("1", 1613382090490L, 55D, 1200D)
    val catBFirstSample = createSample("2", speed = 80D)
    val carAThirdSample = createSample("1", speed = 60D)

    SampleService.add(carAFirstSample)
    SampleService.add(carASecondSample)
    SampleService.add(catBFirstSample)
    SampleService.add(carAThirdSample)

    val result = SampleService.aggregateData("1")

    assert(result.isDefined)

    val stats = result.get

    assert(stats.maximumSpeed == 60.0)
  }

  test("Tests if `aggregateData` method works as expected for last message timestamp.") {
    val carAFirstSample = createSample("1")
    val carASecondSample = createSample("1", 1613382090490L, 55D, 1200D)
    val carAThirdSample = createSample("1", 1613382090494L, 55D, 1200D)

    SampleService.add(carAFirstSample)
    SampleService.add(carASecondSample)
    SampleService.add(carAThirdSample)

    val result = SampleService.aggregateData("1")

    assert(result.isDefined)

    val stats = result.get

    assert(stats.lastMessageTimestamp == 1613382090494L)
  }

  test("Tests if `aggregateData` method works as expected for number of charges.") {
    val carAFirstSample = createSample("1", isCharging = 1D)
    val carASecondSample = createSample("1", isCharging = 1D)
    val carAThirdSample = createSample("1")
    val carAFourthSample = createSample("1", isCharging = 1D)

    SampleService.add(carAFirstSample)
    SampleService.add(carASecondSample)
    SampleService.add(carAThirdSample)
    SampleService.add(carAFourthSample)

    val result = SampleService.aggregateData("1")

    assert(result.isDefined)

    val stats = result.get

    assert(stats.numberOfChargers == 2)
  }

  test("Tests if `aggregateData` method works as expected for vehicle status.") {
    val carAFirstSample = createSample("1", isCharging = 1D)
    val carBFirstSample = createSample("2", speed = 0)
    val carCFirstSample = createSample("3", speed = 0, isCharging = 1)

    SampleService.add(carAFirstSample)
    SampleService.add(carBFirstSample)
    SampleService.add(carCFirstSample)


    val resultA = SampleService.aggregateData("1").get
    val resultB = SampleService.aggregateData("2").get
    val resultC = SampleService.aggregateData("3").get

    assert(resultA.vehicleStatus == VehicleStatus.DRIVING)
    assert(resultB.vehicleStatus == VehicleStatus.PARKED)
    assert(resultC.vehicleStatus == VehicleStatus.CHARGING)
  }

  test("Tests if `aggregatedData` works as expected in case the vehicle id is not present.") {
    val result = SampleService.aggregateData("1")
    assert(result.isEmpty)
  }

    private def createSample(
    vehicleId: String,
    receivedAt: Long = 1613382090489L,
    speed: Double = 50D,
    odometer: Double = 1000D,
    time: Double = 72000000D,
    isCharging: Double = 0
  ) : TelemetrySample = {
    TelemetrySample(
      vehicleId,
      receivedAt,
      SignalValues(speed, odometer, time, isCharging)
    )
  }
}
