package ninja.grimrose.sandbox.infra.gcp

import cats.effect.IO
import ninja.grimrose.sandbox.domain.SlackWebhook
import ninja.grimrose.sandbox.infra.SlackWebhookAdapter

trait GcpSlackWebhookAdapter extends SlackWebhookAdapter {
  import wvlet.airframe._

  private val configName                   = bind[ConfigName]
  private val projectIdAdapter             = bind[ProjectIdAdapter]
  private val encryptedSlackWebhookAdapter = bind[EncryptedSlackWebhookAdapter]
  private val kms                          = bind[GoolgeCloudKms]

  def find: IO[SlackWebhook] = {
    for {
      projectId <- projectIdAdapter.find
      encrypted <- encryptedSlackWebhookAdapter.find(projectId, configName)
      decrypted <- kms.decrypt(encrypted.asCipherText)
    } yield SlackWebhook(decrypted)
  }

}
