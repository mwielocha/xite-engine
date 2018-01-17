package xite.engine.actors

import akka.actor.{Actor, ActorLogging}

object UserActor {
  case object Done
}

class UserActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case _ => sender() ! UserActor.Done
  }
}
