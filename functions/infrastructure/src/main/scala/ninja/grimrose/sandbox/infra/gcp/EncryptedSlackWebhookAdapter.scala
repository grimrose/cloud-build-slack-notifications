package ninja.grimrose.sandbox.infra.gcp

import cats.effect.IO

trait EncryptedSlackWebhookAdapter extends RCLoadEnvSupport {

  override def variableKey: String = "SLACK_WEBHOOK"

  def find(projectId: ProjectId, configName: ConfigName): IO[EncryptedSlackWebhook] = {
    findVariable(projectId, configName).map { variable =>
      EncryptedSlackWebhook(decodedValue(variable))
    }
  }
}
