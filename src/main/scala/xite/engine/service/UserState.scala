package xite.engine.service

import xite.engine.model.{Result, User, UserWithVideo, Video}

trait UserState[F[_]] {

  def register(user: User, videos: Seq[Video]): F[Result[UserWithVideo]]

  def action(userId: User.Id, videoId: Video.Id, actionId: Int): F[Result[UserWithVideo]]

}
