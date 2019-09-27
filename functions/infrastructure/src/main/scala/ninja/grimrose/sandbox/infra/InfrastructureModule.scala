package ninja.grimrose.sandbox.infra

import cats.effect.IO
import hammock.InterpTrans
import hammock.fetch.Interpreter
import ninja.grimrose.sandbox.application.SlackNotifier
import ninja.grimrose.sandbox.application.io.SlackNotifierIO
import ninja.grimrose.sandbox.domain.CloudBuildMessagePublisher
import ninja.grimrose.sandbox.infra.gcp.{
  ConfigName,
  GoogleCloudKms,
  GoogleCloudKmsIO,
  GoogleCloudKmsIdClient,
  GoogleCloudKmsIdClientIO,
  GoogleCloudRCLoadEnvDecoder,
  GoogleCloudRCLoadEnvDecoderIO
}
import wvlet.airframe._

import scala.concurrent.ExecutionContext

object InfrastructureModule {

  def design: Design =
    newDesign
      .bind[ConfigName].toInstance(ConfigName("rc-cloud-build-slack-notifications"))
      .bind[SlackNotifier[IO]].to[SlackNotifierIO]
      .bind[InterpTrans[IO]].toInstance(Interpreter.instance[IO])
      .bind[CloudBuildMessagePublisher[IO]].to[CloudBuildMessagePublisherIO]
      .bind[GoogleCloudRCLoadEnvDecoder[IO]].toInstanceProvider[ExecutionContext](
        executionContext => new GoogleCloudRCLoadEnvDecoderIO(executionContext)
      )
      .bind[GoogleCloudKmsIdClient[IO]].toInstanceProvider[GoogleCloudRCLoadEnvDecoder[IO], ExecutionContext](
        (loadEnvDecoder, executionContext) => new GoogleCloudKmsIdClientIO(loadEnvDecoder, executionContext)
      )
      .bind[GoogleCloudKms[IO]].toInstanceProvider[ConfigName, GoogleCloudKmsIdClient[IO], ExecutionContext] {
        (configName, kmsIdClient, executionContext) =>
          new GoogleCloudKmsIO(configName, kmsIdClient)(executionContext)
      }
}
