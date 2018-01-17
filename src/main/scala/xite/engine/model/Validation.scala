package xite.engine.model

import cats.data.{NonEmptyList, Validated}
import cats.data.Validated.{Invalid, Valid}

trait Validation {

  def check[T](in: T)(condition: T => Boolean, error: String): Validated[Errors, T] = {
    if(condition(in)) Valid(in) else Invalid(NonEmptyList.of(error))
  }
}

object Validation extends Validation
