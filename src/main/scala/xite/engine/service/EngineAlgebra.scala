package xite.engine.service

import xite.engine.model._

trait EngineAlgebra[F[_]] {

  def register(reg: Register): F[Result[UserWithVideo]]

  def action(a: Action): F[Result[UserWithVideo]]

}
