package xite.engine

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import xite.engine.http.RestApi
import xite.engine.repository.InMemoryVideoRepository
import xite.engine.service.DefaultEngineService

object Main extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()

  val videoRepository = new InMemoryVideoRepository

  val engineService = new DefaultEngineService(actorSystem, videoRepository)

  val restApi = new RestApi(engineService)

  implicit val mat: ActorMaterializer = ActorMaterializer()

  Http().bindAndHandle(
    handler = restApi.route,
    interface = "localhost",
    port = 8085
  )
}
