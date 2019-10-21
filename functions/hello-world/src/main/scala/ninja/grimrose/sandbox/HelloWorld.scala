package ninja.grimrose.sandbox

import buildinfo.BuildInfo
import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import wvlet.log.{LogFormatter, LogSupport, Logger}

import scala.concurrent.ExecutionContext
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExportTopLevel

object HelloWorld extends LogSupport {

  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  scalajs.js.Dynamic.global.process.env.NODE_ENV.toString match {
    case "production" => Logger.setDefaultFormatter(LogFormatter.TSVLogFormatter)
    case _            => Logger.setDefaultFormatter(LogFormatter.SourceCodeLogFormatter)
  }

  info(BuildInfo.toJson)

  private val design = HelloWorldModule.design.bind[ExecutionContext].toInstance(queue)

  @JSExportTopLevel("helloWorld")
  val function: HttpFunction = (req, res) => {
    design.withProductionMode.withSession { session =>
      val fetcher = session.build[HttpBinFetcher]

      val task = for {
        _       <- IO(info("NODE_ENV" -> scalajs.js.Dynamic.global.process.env.NODE_ENV))
        _       <- IO(info("originalUrl" -> req.originalUrl))
        _       <- IO(info("body" -> JSON.stringify(req.body)))
        _       <- IO(info("query" -> JSON.stringify(req.query)))
        _       <- IO(info("ips" -> req.ips.asJson.noSpaces))
        _       <- IO(info("User-Agent" -> JSON.stringify(req.headers.`user-agent`)))
        fetched <- fetcher.fetch
        _       <- IO(info(fetched.asJson.noSpaces))
        _       <- IO(res.`type`("json").send(fetched.asJson.noSpaces))
      } yield ()

      task.unsafeRunAsyncAndForget()
    }
  }

}
