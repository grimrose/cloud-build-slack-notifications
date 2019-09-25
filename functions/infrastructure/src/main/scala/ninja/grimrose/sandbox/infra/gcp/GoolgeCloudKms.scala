package ninja.grimrose.sandbox.infra.gcp

import cats.effect.{ContextShift, IO}

import scala.concurrent.ExecutionContext

trait GoolgeCloudKms {
  import wvlet.airframe._

  private val configName = bind[ConfigName]

  private val projectIdAdapter   = bind[ProjectIdAdapter]
  private val locationIdAdapter  = bind[LocationIdAdapter]
  private val keyRingIdAdapter   = bind[KeyRingIdAdapter]
  private val cryptoKeyIdAdapter = bind[CryptoKeyIdAdapter]

  private implicit val ec: ExecutionContext = bind[ExecutionContext]

  import typings.atGoogleDashCloudKms.atGoogleDashCloudKmsMod.v1Ns.{
    KeyManagementServiceClient,
    KeyManagementServiceClientNs
  }

  def decrypt(cipherText: CipherText): IO[String] = {
    for {
      projectId   <- projectIdAdapter.find
      locationId  <- locationIdAdapter.find
      keyRingId   <- keyRingIdAdapter.find(projectId, configName)
      cryptoKeyId <- cryptoKeyIdAdapter.find(projectId, configName)
      decrypted   <- decrypt(projectId, locationId, keyRingId, cryptoKeyId, cipherText)
    } yield decrypted
  }

  private def decrypt(
      projectId: ProjectId,
      locationId: LocationId,
      keyRingId: KeyRingId,
      cryptoKeyId: CryptoKeyId,
      cipherText: CipherText
  ) = {
    implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)

    IO.fromFuture {
      IO {
        val client = new KeyManagementServiceClient()

        val keyPath = client.cryptoKeyPath(projectId.value, locationId.value, keyRingId.value, cryptoKeyId.value)

        val request = new KeyManagementServiceClientNs.DecryptRequest() {
          override var ciphertext: String = cipherText.value
          override var name: String       = keyPath
        }

        client.decrypt(request).toFuture.map(_._1.plaintext.toString("utf-8"))
      }
    }
  }

}
