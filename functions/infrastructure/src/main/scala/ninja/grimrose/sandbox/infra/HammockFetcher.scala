package ninja.grimrose.sandbox.infra

import cats.effect.IO
import ninja.grimrose.sandbox.domain.HttpBinResponse

trait HammockFetcher {
  import hammock._
  import hammock.circe.implicits._
  import hammock.fetch.Interpreter._
  import hammock.hi.Opts
  import hammock.marshalling._
  import io.circe.generic.auto._

  def fetch: IO[HttpBinResponse] = {
    val endpoint = uri"http://httpbin.org/ip"

    Hammock
      .getWithOpts(endpoint, Opts.empty).as[HttpBinResponse]
      .exec[IO]
  }

}
