package ninja.grimrose.sandbox.infra

import cats.effect.IO
import ninja.grimrose.sandbox.domain.SlackWebhook

trait SlackWebhookAdapter {

  def find: IO[SlackWebhook]

}
