package ninja.grimrose.sandbox.infra

import cats.effect.IO
import ninja.grimrose.sandbox.domain.SlackWebhook
import ninja.grimrose.sandbox.infra.gcp.{
  ConfigName,
  EncryptedSlackWebhook,
  GoogleCloudKms,
  GoogleCloudKmsIdClient,
  GoogleCloudRCLoadEnvDecoder
}

trait SlackWebhookAdapter {

  import wvlet.airframe._

  import SlackWebhookAdapter._

  private val configName     = bind[ConfigName]
  private val kmsIdClient    = bind[GoogleCloudKmsIdClient[IO]]
  private val loadEnvDecoder = bind[GoogleCloudRCLoadEnvDecoder[IO]]
  private val kms            = bind[GoogleCloudKms[IO]]

  def find: IO[SlackWebhook] = {
    for {
      projectId <- kmsIdClient.findProjectId
      encrypted <- loadEnvDecoder.decode(projectId, configName, KEY).map(EncryptedSlackWebhook)
      decrypted <- kms.decrypt(encrypted.asCipherText)
    } yield SlackWebhook(decrypted)
  }

}

object SlackWebhookAdapter {
  val KEY = "SLACK_WEBHOOK"
}
