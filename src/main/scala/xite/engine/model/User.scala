package xite.engine.model

import java.util.concurrent.atomic.AtomicLong

import cats.data.Validated.Valid
import shapeless.tag.@@
import xite.engine.utils.StringHelpers.isValidEmail
import cats.implicits._

object User extends Validation {

  private final val genders = Set(1, 2)

  type Id = Long @@ User

  object Id {

    private val nextId = new AtomicLong()

    def apply(in: Long): User.Id = shapeless.tag[User][Long](in)
    def apply(): User.Id = Id(nextId.incrementAndGet())
  }

  def apply(name: String, email: String, age: Int, gender: Int): User = {
    User(Id(), name, email, age, gender)
  }

  def validated(name: String, email: String, age: Int, gender: Int): Result[User] = {
    (
      Valid(name),
      check(email)(isValidEmail, "email is not valid"),
      check(age)(x => x < 121 && x > 4, "age is not valid"),
      check(gender)(genders, "gender is not valid"),
    ).mapN(User.apply).toEither
  }
}

case class User(id: User.Id, name: String, email: String, age: Int, gender: Int)
