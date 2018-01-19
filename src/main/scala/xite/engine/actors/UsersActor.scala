package xite.engine.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cats.data.EitherT
import xite.engine.model._

import scala.concurrent.Future
import akka.pattern._
import akka.util.Timeout
import cats.implicits._

import scala.concurrent.duration._


object UsersActor {

  case class Register(user: User, videos: Seq[Video])
}

class UsersActor extends Actor with ActorLogging {

  import UsersActor._
  import context.dispatcher

  private implicit val timeout: Timeout = Timeout(200 millis)

  private [actors] var userActors = Map.empty[User.Id, ActorRef]

  override def receive: Receive = {

    case Register(user, videoId) =>

      val ref = context.actorOf(Props[UserActor], s"${user.id}")
      userActors = userActors + (user.id -> ref)
      ref forward UserActor.StartWith(user.id, videoId)

    case a: UserActor.Action =>

      val response = (for {
        ref <- EitherT.fromOption[Future](
          userActors.get(a.userId),
          Errors("userId does not exist")
        )
        response <- EitherT((ref ? a)
          .mapTo[Result[UserWithVideo]])
      } yield response).value

      response pipeTo sender
  }
}
