package xite.engine.service

import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout
import cats.data.EitherT
import cats.implicits._
import xite.engine.actors.UsersActor
import xite.engine.model._

import scala.concurrent.Future
import scala.concurrent.duration._

class DefaultRecommendationService(actorSystem: ActorSystem)
  extends RecommendationService[AsyncResult] with Validation {

  import actorSystem.dispatcher

  private implicit val timeout: Timeout = Timeout(200 millis)

  private val users = actorSystem.actorOf(Props[UsersActor], "users")

  private def register(user: User): AsyncResult[UserWithVideo] =
    (users ? UsersActor.Register(user)).mapTo[Result[UserWithVideo]]

  override def register(reg: Register): AsyncResult[UserWithVideo] = {
    (for {
      user <- EitherT.fromEither[Future] {
        User.validated(
          reg.userName,
          reg.email,
          reg.age,
          reg.gender
        )
      }
      response <- EitherT(register(user))
    } yield response).value
  }

  override def action(act: Action): AsyncResult[UserWithVideo] = ???
}
