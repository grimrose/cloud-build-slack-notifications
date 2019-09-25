package ninja.grimrose.sandbox

import org.scalatest.Suite
import wvlet.airframe.Design

trait AirframeSpecSupport { this: Suite =>

  def design: Design = Design.empty
}
