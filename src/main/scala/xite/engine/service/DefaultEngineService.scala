package xite.engine.service

import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout
import cats.data.EitherT
import cats.implicits._
import xite.engine.actors.{UserActor, UsersActor}
import xite.engine.model._
import xite.engine.repository.VideoRepository

import scala.concurrent.Future
import scala.concurrent.duration._

class DefaultEngineService(
  actorSystem: ActorSystem,
  videoRepository: VideoRepository[Future]
) extends EngineService[AsyncResult] {

  import actorSystem.dispatcher

  private implicit val timeout: Timeout = Timeout(200 millis)

  private val users = actorSystem.actorOf(Props[UsersActor], "users")

  private def register(user: User, videos: Seq[Video]): AsyncResult[UserWithVideo] =
    (users ? UsersActor.Register(user, videos)).mapTo[Result[UserWithVideo]]

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
      videos <- EitherT.liftF(videoRepository.getAll)
      response <- EitherT(register(user, videos))
    } yield response).value
  }

  override def action(a: Action): AsyncResult[UserWithVideo] =
    (users ? UserActor.Action(
      a.userId,
      a.videoId,
      a.actionId
    )).mapTo[Result[UserWithVideo]]
}
