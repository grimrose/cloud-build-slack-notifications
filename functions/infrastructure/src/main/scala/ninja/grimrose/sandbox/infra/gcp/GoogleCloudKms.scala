package ninja.grimrose.sandbox.infra.gcp

import cats.effect.{ContextShift, IO}

import scala.concurrent.ExecutionContext

trait GoogleCloudKms[F[_]] {

  def decrypt(cipherText: CipherText): F[String]

}

class GoogleCloudKmsIO(configName: ConfigName, kmsIdClient: GoogleCloudKmsIdClient[IO])(
    implicit val ec: ExecutionContext
) extends GoogleCloudKms[IO] {

  import typings.atGoogleDashCloudKms.atGoogleDashCloudKmsMod.v1Ns.{
    KeyManagementServiceClient,
    KeyManagementServiceClientNs
  }

  def decrypt(cipherText: CipherText): IO[String] = {
    for {
      projectId   <- kmsIdClient.findProjectId
      locationId  <- kmsIdClient.findLocationId
      keyRingId   <- kmsIdClient.findKeyRingId(projectId, configName)
      cryptoKeyId <- kmsIdClient.findCryptoKeyId(projectId, configName)
      decrypted   <- requestDecrypt(projectId, locationId, keyRingId, cryptoKeyId, cipherText)
    } yield decrypted
  }

  private def requestDecrypt(
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
