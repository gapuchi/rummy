import java.util.Date

import caliban.GraphQL.graphQL
import caliban.schema.{GenericSchema, Schema}
import caliban.{GraphQL, Http4sAdapter, RootResolver}
import dao.GameRepo
import graphql.{Mutation, Query}
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

  implicit val winnerSchema: Schema.Typeclass[Winner] = Schema.gen[Winner]
  implicit val loserSchema: Schema.Typeclass[Loser] = Schema.gen[Loser]
  implicit val dateSchema: Schema.Typeclass[Date] = Schema.stringSchema.contramap(_.toString)
  implicit val gameSchema: Schema.Typeclass[Game] = Schema.gen[Game]

  override def run(args: List[String]): URIO[ZEnv, Int] = {

    val repo = new GameRepo

    val queries = Query(
      URIO(repo.getAllGames),
      arg => URIO(repo.getGame(arg.id)),
      URIO(repo.getAllPlayers)
    )

    val mutations = Mutation(
      arg => URIO(repo.addPlayer(arg.id, arg.name))
    )

    val interpreter: GraphQL[Console with Clock, Query, Mutation, Unit] = graphQL(RootResolver(queries, mutations))

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
