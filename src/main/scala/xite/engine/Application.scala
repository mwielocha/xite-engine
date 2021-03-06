package xite.engine

import akka.actor.ActorSystem
import xite.engine.http.RestApi
import xite.engine.repository.InMemoryVideoRepository
import xite.engine.service.{AkkaUserState, EngineInterpreter}
import cats.implicits._
import scala.concurrent.{ExecutionContext, Future}

class Application {

  // in a real project this would be coupled via a dependency injection pattern
  // I usually go with either macwire or guice, personally not a big fan of either cake pattern
  // nor a mix of cake and reader pattern

  implicit val actorSystem: ActorSystem = ActorSystem()

  implicit val dispatcher: ExecutionContext = actorSystem.dispatcher

  val videoRepository = new InMemoryVideoRepository

  val userState = new AkkaUserState(actorSystem)

  val engine = new EngineInterpreter[Future](userState, videoRepository)

  val restApi = new RestApi(engine)

}
