package app.kafka

import app.models.TelemetrySample
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer

/**
 * The Custom Deserializer.
 *
 * @author mbajramovic.
 */
class CustomDeserializer extends Deserializer[TelemetrySample] {

  override def deserialize(topic: String, data: Array[Byte]): TelemetrySample = {
    val mapper = new ObjectMapper()
    val tObject = mapper.readValue(data, classOf[TelemetrySample])
    tObject
  }
}
