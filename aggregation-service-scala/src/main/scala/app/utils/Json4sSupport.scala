package app.utils

import java.lang.Exception
import java.lang.reflect.InvocationTargetException

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model.{ContentTypeRange, MediaType}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import app.models.enumerations.VehicleStatus.VehicleStatus
import org.json4s.JsonAST.{JNull, JString}
import org.json4s.jackson.Serialization
import org.json4s.{CustomSerializer, DefaultFormats, Formats, Serialization}

import scala.collection.immutable.Seq

/**
 * Automatic to and from JSON marshalling/unmarshalling using an in-scope *Json4s* protocol.
 *
 * Pretty printing is enabled if an implicit [[Json4sSupport.ShouldWritePretty.True]] is in scope.
 */
object Json4sSupport extends Json4sSupport {
  implicit val serialization: Serialization.type = Serialization
  implicit val formats: Formats = DefaultFormats + EnumNameSerializer

  sealed abstract class ShouldWritePretty

  final object ShouldWritePretty {
    final object True extends ShouldWritePretty
    final object False extends ShouldWritePretty
  }
}

object EnumNameSerializer extends CustomSerializer[VehicleStatus](format => ({
  case JNull => null
}, {
  case at: VehicleStatus => JString(at.toString)
}))

/**
 * Automatic to and from JSON marshalling/unmarshalling using an in-scope *Json4s* protocol.
 *
 * Pretty printing is enabled if an implicit [[Json4sSupport.ShouldWritePretty.True]] is in scope.
 */
trait Json4sSupport {

  import Json4sSupport._

  def unmarshallerContentTypes: Seq[ContentTypeRange] =
    mediaTypes.map(ContentTypeRange.apply)

  def mediaTypes: Seq[MediaType.WithFixedCharset] =
    List(`application/json`)

  private val jsonStringUnmarshaller =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(unmarshallerContentTypes: _*)
      .mapWithCharset {
        case (ByteString.empty, _) => throw Unmarshaller.NoContentException
        case (data, charset) => data.decodeString(charset.nioCharset.name)
      }

  private val jsonStringMarshaller =
    Marshaller.oneOf(mediaTypes: _*)(Marshaller.stringMarshaller)

  /**
   * HTTP entity => `A`
   *
   * @tparam A type to decode
   * @return unmarshaller for `A`
   */
  implicit def unmarshaller[A: Manifest](implicit serialization: Serialization,
    formats: Formats): FromEntityUnmarshaller[A] =
    jsonStringUnmarshaller
      .map(s => serialization.read(s))

  /**
   * `A` => HTTP entity
   *
   * @tparam A type to encode, must be upper bounded by `AnyRef`
   * @return marshaller for any `A` value
   */
  implicit def marshaller[A <: AnyRef](implicit serialization: Serialization,
    formats: Formats,
    shouldWritePretty: ShouldWritePretty =
    ShouldWritePretty.False
  ): ToEntityMarshaller[A] =
    shouldWritePretty match {
      case ShouldWritePretty.False =>
        jsonStringMarshaller.compose(serialization.write[A])
      case ShouldWritePretty.True =>
        jsonStringMarshaller.compose(serialization.writePretty[A])
    }
}