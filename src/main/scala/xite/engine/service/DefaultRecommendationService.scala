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

class DefaultRecommendationService(
  actorSystem: ActorSystem,
  videoRepository: VideoRepository[AsyncResult]
) extends RecommendationService[AsyncResult] with Validation {

  import actorSystem.dispatcher

  private implicit val timeout: Timeout = Timeout(200 millis)

  private val users = actorSystem.actorOf(Props[UsersActor], "users")

  private def register(user: User, videoId: Video.Id): AsyncResult[UserWithVideo] =
    (users ? UsersActor.Register(user, videoId)).mapTo[Result[UserWithVideo]]

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
      nextVideo <- EitherT(videoRepository.nextVideo)
      response <- EitherT(register(user, nextVideo.id))
    } yield response).value
  }

  private def action(
    userId: User.Id,
    videoId: Video.Id,
    nextVideoId: Video.Id,
    actionId: Int
  ): AsyncResult[UserWithVideo] =
    (users ? UserActor.Action(
      userId,
      videoId,
      nextVideoId,
      actionId
    )).mapTo[Result[UserWithVideo]]

  override def action(a: Action): AsyncResult[UserWithVideo] = {
    (for {
      nextVideo <- EitherT(videoRepository.nextVideo)
      response <- EitherT(action(a.userId, a.videoId, nextVideo.id, a.actionId))
    } yield response).value
  }
}
