package xite.engine.repository

import xite.engine.model.Video

import scala.concurrent.Future
import scala.util.Random

class InMemoryVideoRepository extends VideoRepository[Future] {

  private val videos: Seq[Video] = Random.shuffle(
    (1000 to 5000)
      .map(_ => Video())
  ).take(10)

  override def getAll: Future[Seq[Video]] = Future.successful(videos)
}
