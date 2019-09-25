package ninja.grimrose.sandbox

import org.scalatest.{BeforeAndAfterAll, Suite}
import wvlet.log.{LogFormatter, LogLevel, LogSupport, Logger}

trait SpecLogSupport extends LogSupport with BeforeAndAfterAll { self: Suite =>
  Logger.setDefaultLogLevel(LogLevel.DEBUG)
  Logger.setDefaultFormatter(LogFormatter.SourceCodeLogFormatter)

  override protected def beforeAll(): Unit = {
    Logger.scheduleLogLevelScan
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    Logger.stopScheduledLogLevelScan
    super.afterAll()
  }
}
