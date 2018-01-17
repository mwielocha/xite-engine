package xite.engine.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, _}
import xite.engine.model.{Action, AsyncResult, Register}
import xite.engine.service.EngineService

class RestApi(recommendationService: EngineService[AsyncResult])
  extends DefaultCirceSupport with DefaultCodecs {

  def route: Route = pathPrefix("/") {
    (path("register") & post & entity(as[Register])) { register =>
      complete(recommendationService.register(register))
    } ~ (path("action") & post & entity(as[Action])) { action =>
      complete(recommendationService.action(action))
    }
  }
}
