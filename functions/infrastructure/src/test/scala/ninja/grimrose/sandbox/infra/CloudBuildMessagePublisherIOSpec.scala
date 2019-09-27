package ninja.grimrose.sandbox.infra

import cats.effect.IO
import cats.~>
import hammock.{Entity, HttpF, HttpResponse, InterpTrans, Status}
import ninja.grimrose.sandbox.BaseSpecSupport
import ninja.grimrose.sandbox.domain.{CloudBuildMessage, CloudBuildMessagePublisher}
import ninja.grimrose.sandbox.infra.gcp.{
  CipherText,
  ConfigName,
  CryptoKeyId,
  GoogleCloudKms,
  GoogleCloudKmsIdClient,
  GoogleCloudRCLoadEnvDecoder,
  KeyRingId,
  LocationId,
  ProjectId
}
import org.scalatest.{AsyncFunSpec, EitherValues}
import wvlet.airframe.Design

import scala.concurrent.ExecutionContext

class CloudBuildMessagePublisherIOSpec extends AsyncFunSpec with BaseSpecSupport with EitherValues {

  override implicit def executionContext: ExecutionContext = scalajs.concurrent.JSExecutionContext.Implicits.queue

  override def design: Design =
    InfrastructureModule.design
      .bind[ExecutionContext].toInstance(executionContext)
      .bind[InterpTrans[IO]].toInstance {
        new InterpTrans[IO] {
          override def trans: HttpF ~> IO = new (HttpF ~> IO) {
            override def apply[A](fa: HttpF[A]): IO[A] = IO.pure[A](
              HttpResponse(
                Status.OK,
                Map(),
                Entity.StringEntity("OK")
              ).asInstanceOf[A]
            )
          }
        }
      }
      .bind[GoogleCloudKmsIdClient[IO]].toInstance(new GoogleCloudKmsIdClient[IO] {
        override def findProjectId: IO[ProjectId] = IO.pure(ProjectId("sample"))

        override def findLocationId: IO[LocationId] = IO.raiseError(fail("findLocationId"))

        override def findKeyRingId(projectId: ProjectId, configName: ConfigName): IO[KeyRingId] =
          IO.raiseError(fail("findKeyRingId"))

        override def findCryptoKeyId(projectId: ProjectId, configName: ConfigName): IO[CryptoKeyId] =
          IO.raiseError(fail("findCryptoKeyId"))
      })
      .bind[GoogleCloudRCLoadEnvDecoder[IO]].toInstance(new GoogleCloudRCLoadEnvDecoder[IO] {
        override def decode(projectId: ProjectId, configName: ConfigName, variableKey: String): IO[String] =
          IO.pure("encrypted")
      })
      .bind[GoogleCloudKms[IO]].toInstance(new GoogleCloudKms[IO] {
        override def decrypt(cipherText: CipherText): IO[String] = IO.pure("https://example.com")
      })

  design.withSession { session =>
    val publisher = session.build[CloudBuildMessagePublisher[IO]]

    val message = CloudBuildMessage.build(
      buildId = "test",
      logUrl = "https://console.cloud.google.com",
      status = "SUCCESS",
      badStatus = false,
      startTime = Some("2014-10-02T15:01:23.045123456Z"),
      finishTime = Some("2014-10-02T15:01:23.045123456Z"),
      timeout = "3.5s"
    )

    it("publish") {
      publisher.publish(message).attempt.unsafeToFuture().map { result =>
        assert(result.isRight)
      }
    }
    it("doNothing") {
      publisher.doNothing().attempt.unsafeToFuture().map { result =>
        assert(result.right.value == "do nothing")
      }
    }
  }
}
