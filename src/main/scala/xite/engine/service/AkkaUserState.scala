package xite.engine.service

import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout
import xite.engine.actors.{UserActor, UsersActor}
import xite.engine.model._

import scala.concurrent.Future
import scala.concurrent.duration._

class AkkaUserState(
  actorSystem: ActorSystem
) extends UserState[Future] {

  private implicit val timeout: Timeout = Timeout(200 millis)

  private val users = actorSystem.actorOf(Props[UsersActor], "users")

  override def register(user: User, videos: Seq[Video]): AsyncResult[UserWithVideo] =
    (users ? UsersActor.Register(user, videos)).mapTo[Result[UserWithVideo]]

  override def action(userId: User.Id, videoId: Video.Id, actionId: Int): AsyncResult[UserWithVideo] =
    (users ? UserActor.Action(
      userId,
      videoId,
      actionId
    )).mapTo[Result[UserWithVideo]]

}
