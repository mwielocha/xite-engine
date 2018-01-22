package xite.engine

import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Main extends App {

  // simple boostrap

  val app = new Application

  import app.actorSystem

  implicit val mat: ActorMaterializer = ActorMaterializer()

  Http().bindAndHandle(
    handler = app.restApi.route,
    interface = "localhost",
    port = 8085
  )
}
