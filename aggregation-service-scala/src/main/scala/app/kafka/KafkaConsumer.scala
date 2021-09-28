package app.kafka

import java.time.Duration
import java.util
import java.util.{Collections, Properties}

import app.ApplicationConfiguration
import app.models.TelemetrySample
import app.services.SampleService
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

/**
 * The Kafka Consumer.
 *
 * @author mbajramovic.
 */
object KafkaConsumer {

  private val logger = org.apache.log4j.Logger.getLogger(this.getClass.getName)

  private val kafkaConfiguration = ApplicationConfiguration.kafka

  private val topic = kafkaConfiguration.getString("topic")
  private val groupId = kafkaConfiguration.getString("group.id")

  private val host = kafkaConfiguration.getString("host")
  private val port = kafkaConfiguration.getString("port")
  private val enableAutoCommit = kafkaConfiguration.getString("enable.auto.commit")
  private val autoCommitInterval = kafkaConfiguration.getString("auto.commit.interval.ms")

  def start() : Unit = {
    val props = getProps

    val consumer = new KafkaConsumer[String, TelemetrySample](props)
    consumer.subscribe(util.Arrays.asList(topic))
    logger.info("Subscribed to the KAFKA topic: " + topic)

    while (true) {
      try {
        val records = consumer.poll(Duration.ofMillis(100))
        records.iterator().forEachRemaining { record =>
          try {
            logger.info("Received new telemetry sample.")
            val sample = record.value()
            SampleService.add(sample)

            logger.info("State for vehicle with id = " + sample.getVehicleId + " successfully updated.")
          } catch {
            case e: Exception => logger.info("Failed to save received telemetry sample: " + e)
          }
        }
      } catch {
        case e: Exception => logger.info("Error occurred while trying to fetch kafka records." + e)
      }
    }

  }

  private def getProps : Properties = {
    val props = new Properties()

    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host + ":" + port)
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit)
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[CustomDeserializer].getName)
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    props
  }
}
