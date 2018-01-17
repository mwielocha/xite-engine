package xite.engine

import cats.data.{NonEmptyList, Validated}

import scala.concurrent.Future

package object model {

  type Errors = NonEmptyList[String]

  type Result[T] = Either[Errors, T]

  type AsyncResult[T] = Future[Result[T]]

  type EmptyResult = Result[Unit]

}
