package ninja.grimrose.sandbox

import org.scalatest.{DiagrammedAssertions, OptionValues, Suite}

trait BaseSpecSupport extends DiagrammedAssertions with OptionValues with SpecLogSupport with AirframeSpecSupport {
  this: Suite =>
}
