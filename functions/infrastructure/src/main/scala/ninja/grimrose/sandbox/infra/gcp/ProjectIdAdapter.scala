package ninja.grimrose.sandbox.infra.gcp

import cats.effect.{ContextShift, IO}
import wvlet.log.LogSupport

import scala.concurrent.ExecutionContext
import scala.scalajs.js

trait ProjectIdAdapter extends LogSupport {

  import typings.googleDashAuthDashLibrary.buildSrcAuthGoogleauthMod.GoogleAuthOptions
  import typings.googleDashAuthDashLibrary.googleDashAuthDashLibraryMod.GoogleAuth
  import wvlet.airframe._

  private val ec: ExecutionContext = bind[ExecutionContext]

  private implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)

  def find: IO[ProjectId] = {
    val options = js.Object()
    val auth    = new GoogleAuth(options.asInstanceOf[GoogleAuthOptions])

    IO.fromFuture(IO(auth.getProjectId().toFuture)).map { result =>
      debug("project_id" -> result)
      ProjectId(result)
    }
  }
}
