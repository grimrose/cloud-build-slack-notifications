package ninja.grimrose.sandbox.infra

import cats.effect.IO
import ninja.grimrose.sandbox.BaseSpecSupport
import ninja.grimrose.sandbox.domain.{CloudBuildMessage, CloudBuildMessagePublisher}
import org.scalatest.AsyncFunSpec
import wvlet.airframe.Design

import scala.concurrent.ExecutionContext

class CloudBuildMessagePublisherIOSpec extends AsyncFunSpec with BaseSpecSupport {

  override implicit def executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  override def design: Design = InfrastructureModule.design.bind[ExecutionContext].toInstance(executionContext)

  design.withSession { session =>
    val publisher = session.build[CloudBuildMessagePublisher[IO]]

    ignore("publish") {
      val message = CloudBuildMessage.build(
        buildId = "test",
        logUrl = "https://console.cloud.google.com",
        status = "SUCCESS",
        badStatus = false,
        startTime = "2014-10-02T15:01:23.045123456Z",
        finishTime = "2014-10-02T15:01:23.045123456Z",
        timeout = "3.5s"
      )

      publisher.publish(message).attempt.unsafeToFuture().map { result =>
        debug(result)
        assert(result.isRight)
      }
    }
  }
}
