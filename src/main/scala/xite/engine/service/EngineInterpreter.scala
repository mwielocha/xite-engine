package xite.engine.service

import cats.Monad
import cats.data.EitherT
import xite.engine.actors.UserActor
import xite.engine.model._
import xite.engine.repository.VideoRepository

// showcase of abstraction over container type so that the
// business logic stayes detached from what the moand is exactly

case class EngineInterpreter[F[_]: Monad](
  userState: UserState[F],
  videoRepository: VideoRepository[F]
) extends EngineAlgebra[F] {

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
      // if we add a new video or remove one, all user actors will have to be notified
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
