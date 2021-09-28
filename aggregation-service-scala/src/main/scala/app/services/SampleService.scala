package app.services

import java.util.concurrent.TimeUnit

import app.exceptions.InvalidDataException
import app.models.enumerations.VehicleStatus
import app.models.enumerations.VehicleStatus.VehicleStatus
import app.models.{TelemetrySample, VehicleStats}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

/**
 * The Sample Service class. Used for samples management.
 *
 * @author mbajramovic.
 */
object SampleService {

  private val defaultSpeed : Double = 0

  private var samples: ListBuffer[TelemetrySample] = ListBuffer[TelemetrySample]()

  def add(telemetrySample: TelemetrySample) : Future[TelemetrySample] = Future {
    if (telemetrySample.validate()) {
      samples += telemetrySample
      telemetrySample
    } else {
      throw InvalidDataException("Telemetry validation failed.")
    }
  }(ExecutionContext.global)

  def aggregateData(vehicleId: String) : Option[VehicleStats] = {
    val data = samples.filter(sample => sample.getVehicleId == vehicleId)

    if (data.isEmpty) {
      Option.empty
    } else {
      Option.apply(
        VehicleStats(
          calculateAverageSpeed(data),
          calculateMaximumSpeed(data),
          getLastMessageTimestamp(data),
          getNumberOfCharges(data),
          getVehicleStatus(data)
        )
      )
    }
  }

  def removeAll(): Unit = {
    samples = ListBuffer[TelemetrySample]()
  }

  private def calculateAverageSpeed(data: ListBuffer[TelemetrySample]) : Double = {
    val lastSample = data
      .findLast(sample => sample.getSignalValues.getOdometer >= 0 && sample.getSignalValues.getDrivingTime >= 0)

    if (lastSample.isDefined) {
      val sample = lastSample.get
      val drivingTime = TimeUnit.MILLISECONDS.toHours(sample.getSignalValues.getDrivingTime.toLong)
      if (drivingTime > 0) {
        sample.getSignalValues.getOdometer / drivingTime
      } else {
        defaultSpeed
      }
    } else {
      defaultSpeed
    }
  }

  private def calculateMaximumSpeed(data: ListBuffer[TelemetrySample]): Double = {
    val values = data.map(_.getSignalValues.getCurrentSpeed)
      .filter(_ >= 0)

    if (values.nonEmpty) {
      values.max
    } else {
      defaultSpeed
    }
  }

  private def getLastMessageTimestamp(data: ListBuffer[TelemetrySample]): Long = {
    val lastSample = data(data.length - 1)
    lastSample.getRecordedAt
  }

  private def getNumberOfCharges(data: ListBuffer[TelemetrySample]): Int = {
    val charges = data.map(_.getSignalValues.getIsCharging)
        .filter(_ >= 0);
    var numberOfCharges = 0;

    for (i <- 0 until charges.length - 1) {
      if (charges(i) == 1 && charges(i + 1) == 0 || charges(i) == 0 && charges(i + 1) == 1) {
        numberOfCharges = numberOfCharges + 1;
      }
    }

    numberOfCharges
  }

  private def getVehicleStatus(data: ListBuffer[TelemetrySample]): VehicleStatus = {
    val lastSample = data(data.length - 1)

    if (lastSample.getSignalValues.getCurrentSpeed > 0) {
      VehicleStatus.DRIVING
    } else if (lastSample.getSignalValues.getCurrentSpeed == 0) {
      if (lastSample.getSignalValues.getIsCharging == 1) {
        VehicleStatus.CHARGING
      } else {
        VehicleStatus.PARKED
      }
    } else {
      VehicleStatus.UNKNOWN
    }
  }
}
