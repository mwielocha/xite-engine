package xite.engine.actors

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{Matchers, WordSpecLike}
import xite.engine.model.{User, Video}

class UserActorSpec extends TestKit(ActorSystem()) with WordSpecLike with Matchers {

  import UserActor._

  private val userId = User.Id(1)
  private val videos = Seq(Video(), Video(), Video())

  "User actor" should {

    "initialize own state" in {
      val userActor = TestActorRef[UserActor]
      userActor ! StartWith(userId, videos)
      userActor.underlyingActor.state should contain(
        UserState(
          videos.head.id,
          Seq(
            Views(videos.head.id),
            Views(videos(1).id),
            Views(videos.last.id)
          )
        )
      )
    }

    "update state on first action" in {
      val userActor = TestActorRef[UserActor]
      userActor ! StartWith(userId, videos)
      userActor ! Action(userId, videos.head.id, 1)
      userActor.underlyingActor.state should contain(
        UserState(
          videos(1).id,
          Seq(
            Views(videos(1).id),
            Views(videos.last.id),
            Views(videos.head.id, 1)
          )
        )
      )
    }

    "update state on second action" in {
      val userActor = TestActorRef[UserActor]
      userActor ! StartWith(userId, videos)
      userActor ! Action(userId, videos.head.id, 1)
      userActor ! Action(userId, videos(1).id, 1)
      userActor.underlyingActor.state should contain(
        UserState(
          videos.last.id,
          Seq(
            Views(videos.last.id),
            Views(videos.head.id, 1),
            Views(videos(1).id, 1)
          )
        )
      )
    }

    "update state on third action" in {
      val userActor = TestActorRef[UserActor]
      userActor ! StartWith(userId, videos)
      userActor ! Action(userId, videos.head.id, 1)
      userActor ! Action(userId, videos(1).id, 1)
      userActor ! Action(userId, videos.last.id, 1)
      userActor.underlyingActor.state should contain(
        UserState(
          videos.head.id,
          Seq(
            Views(videos.head.id, 1),
            Views(videos(1).id, 1),
            Views(videos.last.id, 1)
          )
        )
      )
    }
  }
}
