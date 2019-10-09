package ninja.grimrose.sandbox.infra.gcp

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSImport, JSName}

@JSImport("@google-cloud/rcloadenv", JSImport.Namespace)
@js.native
object GoogleCloudRCLoadEnv extends js.Object {
  import typings.googleDashAuthDashLibrary.buildSrcAuthGoogleauthMod.GoogleAuthOptions
  import typings.node.NodeJS.ProcessEnv

  import scala.scalajs.js
  import js._

  @js.native
  trait Variable extends js.Object {
    var name: String             = js.native
    var updateTime: String       = js.native
    var value: String            = js.native
    var text: js.UndefOr[String] = js.native
  }

  @js.native
  trait TransformOptions extends js.Object {
    var only: js.Array[String]          = js.native
    var except: js.Array[String]        = js.native
    var debug: js.UndefOr[Boolean]      = js.native
    var `override`: js.UndefOr[Boolean] = js.native
  }

  @js.native
  trait RCLoadEnvOptions extends GoogleAuthOptions {
    var debug: js.UndefOr[Boolean]      = js.native
    var apiEndpoint: js.UndefOr[String] = js.native
  }

  def getVariables(configName: String, opts: RCLoadEnvOptions): Promise[js.Array[Variable]]            = js.native
  def transform(variables: js.Array[Variable], oldEnv: ProcessEnv, opts: TransformOptions): ProcessEnv = js.native
  @JSName("apply")
  def apply(variables: js.Array[Variable], env: ProcessEnv, opts: js.Any): ProcessEnv               = js.native
  def getAndApply(configName: String, env: ProcessEnv, opts: RCLoadEnvOptions): Promise[ProcessEnv] = js.native

}
