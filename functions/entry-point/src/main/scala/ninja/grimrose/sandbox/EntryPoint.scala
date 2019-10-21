package ninja.grimrose.sandbox

import buildinfo.BuildInfo
import cats.effect.IO
import ninja.grimrose.sandbox.application.SlackNotifier
import ninja.grimrose.sandbox.core.Base64Decoder
import ninja.grimrose.sandbox.domain.PubsubMessage
import ninja.grimrose.sandbox.infra.InfrastructureModule
import wvlet.log.{LogFormatter, LogLevel, LogSupport, Logger}

import scala.concurrent.{ExecutionContext, Promise}
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.{Dictionary, JSON}

object EntryPoint extends LogSupport {

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
  import scala.scalajs.js.JSConverters._

  Logger.setDefaultLogLevel(LogLevel.INFO)
  Logger.setDefaultFormatter(LogFormatter.PlainSourceCodeLogFormatter)

  info(BuildInfo.toJson)

  private val design = InfrastructureModule.design.bind[ExecutionContext].toInstance(queue)

  @JSExportTopLevel("entryPoint")
  val function: BackgroundFunction = event => {
    val promise = Promise[String]()

    design.withProductionMode.withSession { session =>
      info("event" -> JSON.stringify(event))

      val dict = event.asInstanceOf[Dictionary[Any]]

      val pubsubMessage = PubsubMessage(
        dict.get("data").map(_.toString),
        dict.get("attributes").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty[String, String])
      )

      val notifier = session.build[SlackNotifier[IO]]

      val task: IO[Unit] = for {
        decoded  <- IO(Base64Decoder.decode(pubsubMessage.data.getOrElse("")))
        notified <- notifier.notify(decoded)
        _        <- IO(promise.success(notified))
      } yield ()

      task
        .handleErrorWith { exception =>
          promise.failure(exception)
          IO.raiseError(exception)
        }.unsafeRunAsyncAndForget()
    }

    promise.future.toJSPromise
  }

}
