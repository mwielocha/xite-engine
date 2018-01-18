package xite.engine.http

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Printer
import xite.engine.model._

trait DefaultCirceSupport extends FailFastCirceSupport {
  requires: DefaultCodecs =>

  implicit val printer: Printer = Printer(
    preserveOrder = true,
    dropNullValues = true,
    indent = ""
  )

  lazy val errorsMarshaller: ToResponseMarshaller[Errors] = {
    implicitly[ToEntityMarshaller[Errors]].map {
      en => HttpResponse(StatusCodes.BadRequest, entity = en)
    }
  }

  implicit def resultMarshaller[T](implicit mt: ToResponseMarshaller[T]): ToResponseMarshaller[Result[T]] =
    Marshaller[Result[T], HttpResponse] {
      implicit ec => {
        case Right(t) => mt(t)
        case Left(errors) => errorsMarshaller(errors)
      }
    }
}
