package xite.engine.repository

import xite.engine.model.Video

import scala.concurrent.Future

class InMemoryVideoRepository extends VideoRepository[Future] {

  private val videos: Seq[Video] = (1 to 20).map(_ => Video())

  override def getAll: Future[Seq[Video]] = Future.successful(videos)
}
