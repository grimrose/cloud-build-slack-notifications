package ninja.grimrose.sandbox

import org.scalatest.{OptionValues, Suite}
import org.scalatest.diagrams.Diagrams

trait BaseSpecSupport extends Diagrams with OptionValues with SpecLogSupport with AirframeSpecSupport {
  this: Suite =>
}
