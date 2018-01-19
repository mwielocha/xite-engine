package xite.engine.service

import cats.Monad
import cats.data.EitherT
import xite.engine.actors.UserActor
import xite.engine.model._
import xite.engine.repository.VideoRepository

case class EngineInterpreter[F[_]: Monad](
  userState: UserState[F],
  videoRepository: VideoRepository[F]
) extends EngineAlgebra[F] with Validation {

  override def action(a: Action): F[Result[UserWithVideo]] = {
    (for {
      act <- EitherT.fromEither[F] {
        UserActor.Action.validated(
          a.userId,
          a.videoId,
          a.actionId
        )
      }
      response <- EitherT {
        userState.action(
          act.userId,
          act.videoId,
          act.actionId
        )
      }
    } yield response).value
  }

  override def register(reg: Register): F[Result[UserWithVideo]] = {
    (for {
      user <- EitherT.fromEither[F] {
        User.validated(
          reg.userName,
          reg.email,
          reg.age,
          reg.gender
        )
      }
      videos <- EitherT.liftF(videoRepository.getAll)
      response <- EitherT {
        userState.register(
          user,
          videos
        )
      }
    } yield response).value
  }
}
