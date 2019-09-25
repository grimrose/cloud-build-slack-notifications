package ninja.grimrose

import scala.scalajs.js

package object sandbox {
  type BackgroundFunction = js.Function1[js.Any, js.Promise[_]]

}
