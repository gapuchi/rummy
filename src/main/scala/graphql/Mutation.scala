package graphql

import zio.URIO
import zio.clock.Clock

case class Mutation(addPlayer: PlayerArg => URIO[Clock, Boolean])

case class PlayerArg(id: String, name: String)