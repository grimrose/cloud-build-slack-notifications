package ninja.grimrose.sandbox.infra.gcp

import cats.effect.IO
import ninja.grimrose.sandbox.infra.Base64DecodeSupport

trait KeyRingIdAdapter extends RCLoadEnvSupport with Base64DecodeSupport {

  override def variableKey: String = "KEY_RING"

  def find(projectId: ProjectId, configName: ConfigName): IO[KeyRingId] = {
    findVariable(projectId, configName).map { variable =>
      KeyRingId(decodedValue(variable))
    }
  }
}
