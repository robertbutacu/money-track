import cats.effect._
import cats.implicits._
import controllers.HelloWorld
import org.http4s.server.blaze._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(HelloWorld.helloWorldService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
