package xite.engine.repository

import xite.engine.model.Video

trait VideoRepository[F[_]] {

  def getAll: F[Seq[Video]]

}
