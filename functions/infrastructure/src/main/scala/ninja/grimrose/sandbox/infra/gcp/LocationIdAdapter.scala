package ninja.grimrose.sandbox.infra.gcp

import cats.effect.IO

trait LocationIdAdapter {

  def find: IO[LocationId] = IO.pure(LocationId("asia-northeast1"))

}
