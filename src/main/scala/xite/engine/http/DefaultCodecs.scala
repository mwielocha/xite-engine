package xite.engine.http

import io.circe.java8.time.TimeInstances
import io.circe.{Decoder, Encoder, Json}
import shapeless.tag
import shapeless.tag.@@
import xite.engine.model.{Action, Errors, Register, UserWithVideo}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

trait DefaultCodecs extends TimeInstances {

  implicit val errorsEncoder: Encoder[Errors] = {
    errors =>
      Json.obj(
        "errors" -> Json.fromValues {
          errors.map(Json.fromString).toList
        }
      )
  }

  implicit val userWithVideoEncoder: Encoder[UserWithVideo] = deriveEncoder[UserWithVideo]

  implicit val actionDecoder: Decoder[Action] = deriveDecoder[Action]
  implicit val registerDecoder: Decoder[Register] = deriveDecoder[Register]

  implicit def `@@LongDecoder`[T]: Decoder[Long @@ T] = {
    Decoder.decodeLong.map(x => tag[T](x))
  }

  implicit def `@@LongEncoder`[T <: Long]: Encoder[T] = {
    Encoder.encodeLong.contramap(identity)
  }
}
