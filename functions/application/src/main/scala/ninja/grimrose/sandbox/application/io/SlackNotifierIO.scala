package ninja.grimrose.sandbox.application.io

import cats.effect.{IO, Timer}
import ninja.grimrose.sandbox.application.SlackNotifier
import ninja.grimrose.sandbox.domain.{CloudBuildMessage, CloudBuildMessagePublisher}
import wvlet.log.LogSupport

import scala.concurrent.ExecutionContext

/**
 * @see https://cloud.google.com/cloud-build/docs/configure-third-party-notifications
 */
trait SlackNotifierIO extends SlackNotifier[IO] with LogSupport {

  import retry.CatsEffect._
  import retry.RetryPolicies._
  import retry._
  import wvlet.airframe._

  import scala.concurrent.duration._

  private val publisher = bind[CloudBuildMessagePublisher[IO]]
  private val ec        = bind[ExecutionContext]

  def notify(rawJson: RawJson): IO[String] = {
    CloudBuildMessage.parse(rawJson) match {
      case Left(errMsg) =>
        error(errMsg)
        publisher.doNothing()
      case Right(message) =>
        implicit val timer: Timer[IO] = IO.timer(ec)
        val policy                    = limitRetries[IO](3) join exponentialBackoff[IO](10.milliseconds)

        def onError(err: Throwable, details: RetryDetails): IO[Unit] = IO {
          details match {
            case RetryDetails.GivingUp(totalRetries, totalDelay) =>
              error(s"giving up. retry=$totalRetries, delay=$totalDelay", err)
            case RetryDetails.WillDelayAndRetry(nextDelay, retriesSoFar, cumulativeDelay) =>
              error(s"will next=$nextDelay sofar=$retriesSoFar, delay=$cumulativeDelay", err)
          }
        }

        retryingOnAllErrors(policy, onError) {
          publisher.publish(message)
        }
    }
  }

}
