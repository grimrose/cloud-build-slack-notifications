package ninja.grimrose

import typings.expressDashServeDashStaticDashCore.expressDashServeDashStaticDashCoreMod.{Request, Response}

import scala.scalajs.js

package object sandbox {
  type HttpFunction = js.Function2[Request[_, js.Any, js.Any], Response[js.Any], js.Any]
}
