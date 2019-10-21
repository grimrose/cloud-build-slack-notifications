package ninja.grimrose.sandbox

import wvlet.airframe.{Design, newDesign}

object HelloWorldModule {
  def design: Design = newDesign.bind[HttpBinFetcher].toSingleton
}
