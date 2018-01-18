package xite.engine.actors

import akka.actor.{Actor, ActorLogging}
import xite.engine.model._

object UserActor {

  case class Action(
    userId: User.Id,
    videoId: Video.Id,
    actionId: Int
  )

  case class StartWith(userId: User.Id, videos: Seq[Video])

  case object Done

  case class Views(videoId: Video.Id, count: Int = 0) {
    def inc(): Views = copy(count = count + 1)
  }

  object Views {
    implicit val ordering: Ordering[Views] =
      Ordering.by(_.count)
  }

  case class UserState(lastVideoId: Video.Id, history: Seq[Views]) {

    def update(): UserState = {
      val (head +: tail) = history
      val after = (tail :+ head.inc()).sorted
      UserState(after.head.videoId, after)
    }
  }

  object UserState {

    def apply(videos: Seq[Video]): UserState = {
      val (head +: tail) = videos.map(v => Views(v.id))
      UserState(head.videoId, head +: tail)
    }
  }
}

class UserActor extends Actor with ActorLogging {

  import UserActor._

  private var state: Option[UserState] = None

  private def response(userId: User.Id): Result[UserWithVideo] = {
    state match {
      case Some(s) => Right(UserWithVideo(userId, s.lastVideoId))
      case None => Left(Errors("Corrupted user state"))
    }
  }

  override def receive: Receive = {

    case StartWith(userId, videos) =>

      state = Some(UserState(videos))

      context.become {

        case Action(uid, vid, _) =>

          val result: Result[UserWithVideo] =
            if(state.map(_.lastVideoId).contains(vid)) {
              state = state.map(_.update())
              response(uid)
            } else Left(Errors("video does not correspond to last given"))

          sender() ! result
      }

      sender() ! response(userId)

    case x => unhandled(x)
  }
}
