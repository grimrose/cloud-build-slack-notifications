package ninja.grimrose.sandbox.infra

import cats.effect.IO
import ninja.grimrose.sandbox.domain.{CloudBuildMessage, CloudBuildMessagePublisher}
import wvlet.log.LogSupport


trait CloudBuildMessagePublisherIO extends CloudBuildMessagePublisher[IO] with LogSupport {

  import hammock._
  import hammock.circe.implicits._
  import hammock.fetch.Interpreter._
  import hammock.hi.Opts
  import wvlet.airframe._

  private val slackWebhookAdapter = bind[SlackWebhookAdapter]

  override def publish(message: CloudBuildMessage): IO[String] = {
    for {
      endpoint <- slackWebhookAdapter.find.map(webhook => uri"${webhook.value}")
      response <- Hammock.postWithOpts(endpoint, Opts.empty, Some(message)).exec[IO]
    } yield {
      response.entity.cata(
        _.toString,
        bin => new String(bin.content),
        _ => ""
      )
    }
  }

  override def doNothing(): IO[String] = IO.pure("do nothing")
}
