package ninja.grimrose.sandbox.infra.gcp

import cats.effect.IO

trait CryptoKeyIdAdapter extends RCLoadEnvSupport {
  override def variableKey: String = "CRYPTO_KEY"

  def find(projectId: ProjectId, configName: ConfigName): IO[CryptoKeyId] = {
    findVariable(projectId, configName).map { variable =>
      CryptoKeyId(decodedValue(variable))
    }
  }
}
