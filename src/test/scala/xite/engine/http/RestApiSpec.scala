package xite.engine.http

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import xite.engine.Application
import xite.engine.model._

class RestApiSpec extends WordSpec with Matchers
  with ScalatestRouteTest with DefaultCodecs with DefaultCirceSupport {

  private val app = new Application

  "Rest api" should {

    "validate user email on registration" in {

      val request = Register("John", "wrong email", 22, 1)

      Post("/register", marshal(request)) ~> app.restApi.route ~> check {
        response.status.intValue() shouldBe 400
        entityAs[Errors] shouldBe Errors("email is not valid")
      }
    }

    "validate user email and age on registration" in {

      val request = Register("John", "wrong email", 1, 1)

      Post("/register", marshal(request)) ~> app.restApi.route ~> check {
        response.status.intValue() shouldBe 400
        entityAs[Errors] shouldBe Errors("email is not valid", "age is not valid")
      }
    }

    "validate user email, age and gender on registration" in {

      val request = Register("John", "wrong email", 1, 13)

      Post("/register", marshal(request)) ~> app.restApi.route ~> check {
        response.status.intValue() shouldBe 400
        entityAs[Errors] shouldBe Errors("email is not valid", "age is not valid", "gender is not valid")
      }
    }

    "register user" in {

      val request = Register("John", "john@email.com", 22, 1)

      Post("/register", marshal(request)) ~> app.restApi.route ~> check {
        response.status.intValue() shouldBe 200
      }
    }

    "fail if user doesn't exist" in {

      val registerRequest = Register("John", "john@email.com", 22, 1)

      val UserWithVideo(_, videoId) = {
        Post("/register", marshal(registerRequest)) ~> app.restApi.route ~> check {
          response.status.intValue() shouldBe 200
          entityAs[UserWithVideo]
        }
      }

      val actionRequest = Action(User.Id(-1), videoId, 1)

      Post("/action", marshal(actionRequest)) ~> app.restApi.route ~> check {
        response.status.intValue() shouldBe 400
        entityAs[Errors] shouldBe Errors("userId does not exist")
      }
    }

    "fail if last video is different" in {

      val registerRequest = Register("John", "john@email.com", 22, 1)

      val UserWithVideo(userId, _) = {
        Post("/register", marshal(registerRequest)) ~> app.restApi.route ~> check {
          response.status.intValue() shouldBe 200
          entityAs[UserWithVideo]
        }
      }

      val actionRequest = Action(userId, Video.Id(-1), 1)

      Post("/action", marshal(actionRequest)) ~> app.restApi.route ~> check {
        response.status.intValue() shouldBe 400
        entityAs[Errors] shouldBe Errors("video does not correspond to last given")
      }
    }

    "perform and action" in {

      val registerRequest = Register("John", "john@email.com", 22, 1)

      val UserWithVideo(userId, videoId) = {
        Post("/register", marshal(registerRequest)) ~> app.restApi.route ~> check {
          response.status.intValue() shouldBe 200
          entityAs[UserWithVideo]
        }
      }

      val actionRequest = Action(userId, videoId, 1)
      
      Post("/action", marshal(actionRequest)) ~> app.restApi.route ~> check {
        response.status.intValue() shouldBe 200
        entityAs[UserWithVideo].videoId should not be videoId
      }
    }
  }
}
