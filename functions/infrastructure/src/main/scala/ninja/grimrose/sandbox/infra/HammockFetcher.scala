package ninja.grimrose.sandbox.infra

import cats.effect.{ContextShift, IO}
import ninja.grimrose.sandbox.domain.HttpBinResponse

import scala.concurrent.ExecutionContext

trait HammockFetcher {
  import hammock._
  import hammock.circe.implicits._
  import hammock.fetch.Interpreter._
  import hammock.hi.Opts
  import hammock.marshalling._
  import io.circe.generic.auto._
  import wvlet.airframe._

  private val executionContext = bind[ExecutionContext]

  def fetch: IO[HttpBinResponse] = {
    val endpoint = uri"http://httpbin.org/ip"

    implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)

    Hammock
      .getWithOpts(endpoint, Opts.empty).as[HttpBinResponse]
      .exec[IO]
  }

}
