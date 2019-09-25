package ninja.grimrose.sandbox.domain

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

// {"@type":"type.googleapis.com/google.pubsub.v1.PubsubMessage","attributes":null,"data":"eyJtZXNzYWdlIjoidHJpZ2dlcjIifQ=="}
case class PubsubMessage(data: Option[String], attributes: Map[String, String])

object PubsubMessage {
  implicit val messageDecoder: Decoder[PubsubMessage] = deriveDecoder
  implicit val messageEncoder: Encoder[PubsubMessage] = deriveEncoder
}
