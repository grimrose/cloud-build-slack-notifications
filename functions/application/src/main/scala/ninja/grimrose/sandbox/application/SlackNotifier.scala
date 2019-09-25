package ninja.grimrose.sandbox.application

trait SlackNotifier[F[_]] {

  type RawJson = String

  def notify(rawJson: RawJson): F[String]

}
