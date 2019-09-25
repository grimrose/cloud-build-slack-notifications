package ninja.grimrose.sandbox.infra.gcp

import cats.effect.{ContextShift, IO}
import ninja.grimrose.sandbox.infra.Base64DecodeSupport
import wvlet.log.LogSupport

import scala.concurrent.ExecutionContext
import scala.scalajs.js

trait RCLoadEnvSupport extends LogSupport with Base64DecodeSupport {

  import GoogleCloudRCLoadEnv._
  import wvlet.airframe._

  private val executionContext: ExecutionContext = bind[ExecutionContext]

  protected def variableKey: String

  protected def targetVariableKey(projectId: ProjectId, configName: ConfigName): String = {
    s"projects/${projectId.value}/configs/${configName.value}/variables/$variableKey"
  }

  protected def findVariable(projectId: ProjectId, configName: ConfigName): IO[Variable] = {
    implicit val contextShift: ContextShift[IO] = IO.contextShift(executionContext)
    val options                                 = js.Object().asInstanceOf[RCLoadEnvOptions]

    IO.fromFuture(IO(getVariables(configName.value, options).toFuture)).flatMap { variables =>
      variables
        .find(_.name == targetVariableKey(projectId, configName))
        .map(IO.pure)
        .getOrElse(IO.raiseError(new IllegalArgumentException(s"$variableKey not found")))
    }
  }

  protected def decodedValue(variable: Variable): String = {
    val value = decode(variable.value)
    debug(s"$variableKey" -> value)
    value
  }
}
