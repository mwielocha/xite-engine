package xite.engine.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import xite.engine.model.{Action, Register}
import xite.engine.service.EngineAlgebra

import scala.concurrent.Future

class RestApi(recommendationService: EngineAlgebra[Future])
  extends DefaultCirceSupport with DefaultCodecs {

  def route: Route = {
    (path("register") & post & entity(as[Register])) { register =>
      complete(recommendationService.register(register))
    } ~ (path("action") & post & entity(as[Action])) { action =>
      complete(recommendationService.action(action))
    }
  }
}
