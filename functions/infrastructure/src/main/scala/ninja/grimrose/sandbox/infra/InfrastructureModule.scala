package ninja.grimrose.sandbox.infra

import cats.effect.IO
import hammock.InterpTrans
import hammock.fetch.Interpreter
import ninja.grimrose.sandbox.application.SlackNotifier
import ninja.grimrose.sandbox.application.io.SlackNotifierIO
import ninja.grimrose.sandbox.domain.CloudBuildMessagePublisher
import ninja.grimrose.sandbox.infra.gcp.{ConfigName, GcpSlackWebhookAdapter}
import wvlet.airframe._

object InfrastructureModule {

  def design: Design =
    newDesign
      .bind[ConfigName].toInstance(ConfigName("rc-cloud-build-slack-notifications"))
      .bind[SlackNotifier[IO]].to[SlackNotifierIO]
      .bind[SlackWebhookAdapter].to[GcpSlackWebhookAdapter]
      .bind[InterpTrans[IO]].toInstance(Interpreter.instance[IO])
      .bind[CloudBuildMessagePublisher[IO]].to[CloudBuildMessagePublisherIO]

}
