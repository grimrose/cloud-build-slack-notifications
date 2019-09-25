package ninja.grimrose.sandbox.domain

trait CloudBuildMessagePublisher[F[_]] {

  def publish(message: CloudBuildMessage): F[String]

  def doNothing(): F[String]

}
