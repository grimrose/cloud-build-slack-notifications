package ninja.grimrose.sandbox.infra

import cats.effect.IO
import ninja.grimrose.sandbox.BaseSpecSupport
import ninja.grimrose.sandbox.domain.SlackWebhook
import ninja.grimrose.sandbox.infra.gcp._
import org.scalatest.EitherValues

import scala.concurrent.ExecutionContext
import org.scalatest.funspec.AsyncFunSpec

class SlackWebhookAdapterSpec extends AsyncFunSpec with BaseSpecSupport with EitherValues {

  import wvlet.airframe._

  override implicit def executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  override def design: Design =
    super.design
      .bind[ConfigName].toInstance(ConfigName("test"))
      .bind[ExecutionContext].toInstance(executionContext)

  it("should be found") {
    design
      .bind[GoogleCloudKmsIdClient[IO]].toInstance(new GoogleCloudKmsIdClient[IO] {
        override def findProjectId: IO[ProjectId] = IO.pure(ProjectId("sample"))

        override def findLocationId: IO[LocationId] = IO.raiseError(fail("findLocationId"))

        override def findKeyRingId(projectId: ProjectId, configName: ConfigName): IO[KeyRingId] =
          IO.raiseError(fail("findKeyRingId"))

        override def findCryptoKeyId(projectId: ProjectId, configName: ConfigName): IO[CryptoKeyId] =
          IO.raiseError(fail("findCryptoKeyId"))
      })
      .bind[GoogleCloudRCLoadEnvDecoder[IO]].toInstance(new GoogleCloudRCLoadEnvDecoder[IO] {
        override def decode(projectId: ProjectId, configName: ConfigName, variableKey: String): IO[String] = {
          assert(variableKey == SlackWebhookAdapter.KEY)

          IO.pure("encrypted")
        }
      })
      .bind[GoogleCloudKms[IO]].toInstance(new GoogleCloudKms[IO] {
        override def decrypt(cipherText: CipherText): IO[String] = {
          assert(cipherText == CipherText("encrypted"))

          IO.pure("decrypted")
        }
      })
      .withSession { session =>
        val adapter = session.build[SlackWebhookAdapter]

        adapter.find.attempt.unsafeToFuture().map { result =>
          assert(result.isRight)
          assert(result.right.value == SlackWebhook("decrypted"))
        }
      }
  }

}
