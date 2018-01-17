package xite.engine.repository

import xite.engine.model.Video

import scala.concurrent.Future
import scala.util.Random

class DefaultVideoRepository extends VideoRepository[Future] {

  private val videos: Seq[Video] = (1 to 20).map(_ => Video())

  // todo: implement
  override def nextVideo: Future[Video] = {
    Future.successful {
      Random.shuffle(videos).head
    }
  }
}
