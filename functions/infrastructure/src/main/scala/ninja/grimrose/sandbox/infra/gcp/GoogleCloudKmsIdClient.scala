package ninja.grimrose.sandbox.infra.gcp

import cats.effect.{ContextShift, IO}
import typings.googleDashAuthDashLibrary.buildSrcAuthGoogleauthMod.GoogleAuthOptions
import typings.googleDashAuthDashLibrary.googleDashAuthDashLibraryMod.GoogleAuth
import wvlet.log.LogSupport

import scala.concurrent.ExecutionContext
import scala.scalajs.js

trait GoogleCloudKmsIdClient[F[_]] {

  def findProjectId: F[ProjectId]

  def findLocationId: F[LocationId]

  def findKeyRingId(projectId: ProjectId, configName: ConfigName): F[KeyRingId]

  def findCryptoKeyId(projectId: ProjectId, configName: ConfigName): F[CryptoKeyId]

}

class GoogleCloudKmsIdClientIO(
    loadEnvDecoder: GoogleCloudRCLoadEnvDecoder[IO],
    executionContext: ExecutionContext
) extends GoogleCloudKmsIdClient[IO]
    with LogSupport {

  def findProjectId: IO[ProjectId] = {
    implicit val contextShift: ContextShift[IO] = IO.contextShift(executionContext)
    val options                                 = js.Object()
    val auth                                    = new GoogleAuth(options.asInstanceOf[GoogleAuthOptions])

    IO.fromFuture(IO(auth.getProjectId().toFuture)).map { result =>
      debug("project_id" -> result)
      ProjectId(result)
    }
  }

  def findLocationId: IO[LocationId] = IO.pure(LocationId("asia-northeast1"))

  def findKeyRingId(projectId: ProjectId, configName: ConfigName): IO[KeyRingId] = {
    loadEnvDecoder.decode(projectId, configName, "KEY_RING").map(KeyRingId)
  }

  def findCryptoKeyId(projectId: ProjectId, configName: ConfigName): IO[CryptoKeyId] = {
    loadEnvDecoder.decode(projectId, configName, "CRYPTO_KEY").map(CryptoKeyId)
  }

}
