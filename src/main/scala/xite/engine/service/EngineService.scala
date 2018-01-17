package xite.engine.service

import xite.engine.model.{Action, Register, UserWithVideo}

trait EngineService[F[_]] {

  def register(reg: Register): F[UserWithVideo]

  def action(act: Action): F[UserWithVideo]

}
