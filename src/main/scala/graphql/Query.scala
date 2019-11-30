package graphql

import model.{Game, Player}
import zio.URIO
import zio.clock.Clock

case class Query(games: URIO[Clock, Set[Game]],
                 game: GameIdArg => URIO[Clock, Option[Game]],
                 players: URIO[Clock, Set[Player]])

case class GameIdArg(id: String)