package ninja.grimrose.sandbox.application.io

import cats.effect.IO
import ninja.grimrose.sandbox.BaseSpecSupport
import ninja.grimrose.sandbox.application.SlackNotifier
import ninja.grimrose.sandbox.domain.{CloudBuildMessage, CloudBuildMessagePublisher}
import wvlet.airframe.Design

import scala.concurrent.ExecutionContext
import org.scalatest.funspec.AsyncFunSpec

class SlackNotifierIOSpec extends AsyncFunSpec with BaseSpecSupport {

  override implicit def executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  override def design: Design =
    super.design
      .bind[ExecutionContext].toInstance(executionContext)
      .bind[SlackNotifier[IO]].to[SlackNotifierIO]
      .bind[CloudBuildMessagePublisher[IO]].toInstance(new CloudBuildMessagePublisher[IO] {
        override def publish(message: CloudBuildMessage): IO[String] = IO {
          assert(message.text.nonEmpty)
          assert(message.attachments.nonEmpty)

          "published"
        }

        override def doNothing(): IO[String] = IO.pure("do nothing")
      })

  design.build[SlackNotifier[IO]] { notifier =>
    it("notify") {

      val raw =
        """|{
           |  "status": "FAILURE",
           |  "id": "test",
           |  "logUrl": "https://example.com",
           |  "startTime": "2014-10-02T15:01:23.045123456Z",
           |  "finishTime": "2014-10-02T15:01:23.045123456Z",
           |  "timeout": "3.5s"
           |}""".stripMargin

      notifier.notify(raw).attempt.unsafeToFuture().map { result =>
        debug(result)

        assert(result.isRight)
      }
    }
  }

}
