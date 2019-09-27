package ninja.grimrose.sandbox.infra.gcp

import cats.effect.{ContextShift, IO}
import ninja.grimrose.sandbox.infra.Base64DecodeSupport
import wvlet.log.LogSupport

import scala.concurrent.ExecutionContext
import scala.scalajs.js

trait GoogleCloudRCLoadEnvDecoder[F[_]] {

  def decode(projectId: ProjectId, configName: ConfigName, variableKey: String): F[String]
}

class GoogleCloudRCLoadEnvDecoderIO(executionContext: ExecutionContext)
    extends GoogleCloudRCLoadEnvDecoder[IO]
    with LogSupport
    with Base64DecodeSupport {

  def decode(projectId: ProjectId, configName: ConfigName, variableKey: String): IO[String] = {
    findVariable(projectId, configName, variableKey).map(variableToString(_, variableKey))
  }

  import GoogleCloudRCLoadEnv._

  private def findVariable(projectId: ProjectId, configName: ConfigName, variableKey: String): IO[Variable] = {
    implicit val contextShift: ContextShift[IO] = IO.contextShift(executionContext)
    val options                                 = js.Object().asInstanceOf[RCLoadEnvOptions]

    IO.fromFuture(IO(getVariables(configName.value, options).toFuture)).flatMap { variables =>
      variables
        .find(_.name == targetVariableKey(projectId, configName, variableKey))
        .map(IO.pure)
        .getOrElse(IO.raiseError(new IllegalArgumentException(s"$variableKey not found")))
    }
  }

  private def targetVariableKey(projectId: ProjectId, configName: ConfigName, variableKey: String): String = {
    s"projects/${projectId.value}/configs/${configName.value}/variables/$variableKey"
  }

  private def variableToString(variable: Variable, variableKey: String): String = {
    val value = decode(variable.value)
    debug(s"$variableKey" -> value)
    value
  }

}
