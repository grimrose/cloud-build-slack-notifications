package ninja.grimrose.sandbox.infra.gcp

import ninja.grimrose.sandbox.BaseSpecSupport
import org.scalatest.AsyncFunSpec

import scala.concurrent.ExecutionContext
import scala.scalajs.js.JSON

class GoogleCloudRCLoadEnvSpec extends AsyncFunSpec with BaseSpecSupport {
  override implicit def executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  import scalajs.js

  it("rcloadenv") {
    import GoogleCloudRCLoadEnv._

    val options = js.Dynamic.literal("debug" -> true)

    getVariables(
      configName = "rc-cloud-build-slack-notifications",
      opts = options.asInstanceOf[RCLoadEnvOptions]
    ).toFuture.map { variables =>
      debug("results" -> JSON.stringify(variables))
      assert(variables.nonEmpty)
    }
  }

}
