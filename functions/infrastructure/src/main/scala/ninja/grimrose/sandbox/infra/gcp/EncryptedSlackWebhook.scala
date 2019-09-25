package ninja.grimrose.sandbox.infra.gcp

case class EncryptedSlackWebhook(value: String) {
  def asCipherText: CipherText = CipherText(value)
}
