package ninja.grimrose.sandbox.infra

import ninja.grimrose.sandbox.BaseSpecSupport
import org.scalatest.AsyncFunSpec

import scala.concurrent.ExecutionContext

class SlackWebhookAdapterSpec extends AsyncFunSpec with BaseSpecSupport {

  import wvlet.airframe._

  override implicit def executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  override def design: Design = InfrastructureModule.design.bind[ExecutionContext].toInstance(executionContext)

  it("should be found") {
    design.withSession { session =>
      val adapter = session.build[SlackWebhookAdapter]

      adapter.find.attempt.unsafeToFuture().map { result =>
        debug(result)
        assert(result.isRight)
      }
    }
  }

}
