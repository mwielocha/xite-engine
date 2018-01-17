package xite.engine

import cats.data.NonEmptyList

import scala.concurrent.Future

package object model {

  type Errors = NonEmptyList[String]

  object Errors {
    def apply(head: String, tail: String*): Errors = NonEmptyList.of(head, tail: _*)
  }

  type Result[T] = Either[Errors, T]

  type AsyncResult[T] = Future[Result[T]]

  type EmptyResult = Result[Unit]

}
