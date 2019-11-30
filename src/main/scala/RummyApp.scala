import java.util.Date

import caliban.GraphQL.graphQL
import caliban.schema.{GenericSchema, Schema}
import caliban.{GraphQL, Http4sAdapter, RootResolver}
import dao.GameRepo
import model.{Game, Loser, Winner}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import zio._
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.interop.catz._

object RummyApp extends CatsApp with GenericSchema[Console with Clock] {

  type RummyTask[A] = RIO[Console with Clock, A]


  case class GameIdArg(id: String)

  case class Query(games: URIO[Console, Set[Game]],
                   game: GameIdArg => URIO[Console, Option[Game]])

  implicit val winnerSchema: Schema.Typeclass[Winner] = Schema.gen[Winner]
  implicit val loserSchema: Schema.Typeclass[Loser] = Schema.gen[Loser]
  implicit val dateSchema: Schema[Any, Date] = Schema.stringSchema.contramap(_.toString)
  implicit val gameSchema: Schema.Typeclass[Game] = Schema.gen[Game]

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {

    val repo = new GameRepo

    val queries: Query = Query(
      URIO(repo.getAllGames),
      arg => URIO(repo.getGame(arg.id))
    )

    val interpreter: GraphQL[Console with Clock, Query, Unit, Unit] = graphQL(RootResolver(queries))

    BlazeServerBuilder[RummyTask]
      .bindHttp(8081, "localhost")
      .withHttpApp(Router(
        "/api/graphql" -> Http4sAdapter.makeRestService(interpreter)
      ).orNotFound)
      .resource
      .toManaged
      .useForever
      .catchAll(err => putStrLn(err.toString).as(1))
  }
}
