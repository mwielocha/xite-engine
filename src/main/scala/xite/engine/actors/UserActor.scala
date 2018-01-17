package xite.engine.actors

import akka.actor.{Actor, ActorLogging}
import cats.data.NonEmptyList
import xite.engine.model._

object UserActor {

  case class Action(
    userId: User.Id,
    videoId: Video.Id,
    nextVideoId: Video.Id,
    actionId: Int
  )

  case class StartWith(videoId: Video.Id)

  case object Done
}

class UserActor extends Actor with ActorLogging {

  import UserActor._

  private var lastVideoId: Option[Video.Id] = None

  override def receive: Receive = {

    case StartWith(videoId) =>

      lastVideoId = Some(videoId)

      context.become {

        case Action(_, id, nextVideoId, _) =>

          val response: Result[Done.type] =
            if(lastVideoId.contains(id)) {
              lastVideoId = Some(nextVideoId)
              Right(Done)
            } else Left(NonEmptyList.of("video does not correspond to last given"))

          sender() ! response
      }

    case x => unhandled(x)
  }
}
