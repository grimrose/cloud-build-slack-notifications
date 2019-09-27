package ninja.grimrose.sandbox.domain

import wvlet.log.LogSupport

case class CloudBuildMessage(
    text: String,
    attachments: Seq[CloudBuildMessage.BuildAttachment]
)

object CloudBuildMessage extends LogSupport {

  import cats.data.Validated
  import cats.data.Validated._
  import cats.implicits._
  import io.circe._
  import io.circe.generic.extras.Configuration
  import io.circe.generic.extras.semiauto._
  import io.circe.syntax._

  implicit val jsonConfig: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val encoder: Encoder[CloudBuildMessage] = deriveConfiguredEncoder
  implicit val decoder: Decoder[CloudBuildMessage] = deriveConfiguredDecoder

  implicit val attachmentEncoder: Encoder[BuildAttachment] = deriveConfiguredEncoder
  implicit val attachmentDecoder: Decoder[BuildAttachment] = deriveConfiguredDecoder

  implicit val fieldEncoder: Encoder[Field] = deriveConfiguredEncoder
  implicit val fieldDecoder: Decoder[Field] = deriveConfiguredDecoder

  type RawJson = String

  private val targetStatuses = Seq("SUCCESS", "FAILURE", "INTERNAL_ERROR", "TIMEOUT", "CANCELLED")

  def parse(rawJson: RawJson): Either[RawJson, CloudBuildMessage] = {

    // https://cloud.google.com/cloud-build/docs/api/reference/rest/Shared.Types/Build
    val json = parser.parse(rawJson).getOrElse(Json.Null)
    debug(json.spaces2)

    val cursor = json.hcursor

    val validated = List(
      validate(cursor, "status").toValidatedNel,
      validate(cursor, "id").toValidatedNel,
      validate(cursor, "logUrl").toValidatedNel,
      validate(cursor, "timeout").toValidatedNel
    ).sequence

    validated match {
      case Valid(values) =>
        values match {
          case status :: id :: logUrl :: timeout :: Nil =>
            if (targetStatuses.contains(status)) {
              val startTime  = cursor.get[String]("startTime").toOption
              val finishTime = cursor.get[String]("finishTime").toOption

              Right(build(id, logUrl, status, status != "SUCCESS", startTime, finishTime, timeout))
            } else {
              Left(Map("msg" -> "not target status", "status" -> status).asJson.noSpaces)
            }
          case _ => Left(Map("msg" -> "valid but not match").asJson.noSpaces)
        }
      case Invalid(e) =>
        val messages = e.toList.toMap
        Left(messages.asJson.noSpaces)
    }
  }

  private def validate(cursor: HCursor, key: String) = {
    Validated
      .fromEither(cursor.get[String](key))
      .leftMap(fail => key -> fail.message)
  }

  def build(
      buildId: String,
      logUrl: String,
      status: String,
      badStatus: Boolean,
      startTime: Option[String],
      finishTime: Option[String],
      timeout: String
  ): CloudBuildMessage = {

    val fields = Seq(
      Some(
        Field(
          title = "Status",
          value = status
        )
      ),
      startTime.map { time =>
        Field(
          title = "StartTime",
          value = time,
          short = true
        )
      },
      finishTime.map { time =>
        Field(
          title = "FinishTime",
          value = time,
          short = true
        )
      },
      Some(
        Field(
          title = "timeout",
          value = timeout,
          short = true
        )
      )
    ).flatten

    CloudBuildMessage(
      text = s"Build `$buildId`",
      attachments = Seq(
        BuildAttachment(
          title = "Build logs",
          titleLink = logUrl,
          color = if (badStatus) "danger" else "good",
          fields = fields
        )
      )
    )
  }

  case class BuildAttachment(
      title: String,
      titleLink: String,
      color: String,
      fields: Seq[Field]
  )

  case class Field(
      title: String,
      value: String,
      short: Boolean = false
  )

}
