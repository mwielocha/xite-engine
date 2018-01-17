package xite.engine.actors

import akka.actor.{Actor, ActorLogging}
import xite.engine.model._

object UserActor {

  case class Action(
    userId: User.Id,
    videoId: Video.Id,
    nextVideoId: Video.Id,
    actionId: Int
  )

  case class StartWith(userId: User.Id, videoId: Video.Id)

  case object Done
}

class UserActor extends Actor with ActorLogging {

  import UserActor._

  private var lastVideoId: Option[Video.Id] = None

  override def receive: Receive = {

    case StartWith(userId, videoId) =>

      lastVideoId = Some(videoId)

      context.become {

        case Action(uid, id, nextVideoId, _) =>

          val response: Result[UserWithVideo] =
            if(lastVideoId.contains(id)) {
              lastVideoId = Some(nextVideoId)
              Right(UserWithVideo(uid, nextVideoId))
            } else Left(Errors("video does not correspond to last given"))

          sender() ! response
      }

      sender() ! Right(UserWithVideo(userId, videoId))

    case x => unhandled(x)
  }
}
