package xite.engine

import akka.actor.ActorSystem
import xite.engine.http.RestApi
import xite.engine.repository.InMemoryVideoRepository
import xite.engine.service.DefaultEngineService

class Application {

  implicit val actorSystem: ActorSystem = ActorSystem()

  val videoRepository = new InMemoryVideoRepository

  val engineService = new DefaultEngineService(actorSystem, videoRepository)

  val restApi = new RestApi(engineService)

}
