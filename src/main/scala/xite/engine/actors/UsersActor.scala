package xite.engine.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import xite.engine.model.User


object UsersActor {

  case class Register(user: User)

}

class UsersActor extends Actor with ActorLogging {

  import UsersActor._

  private var userActors = Map.empty[User.Id, ActorRef]

  override def receive: Receive = {

    case Register(user) =>

      val ref = context.actorOf(Props[UserActor], s"${user.id}")
      userActors = userActors + (user.id -> ref)

  }
}
