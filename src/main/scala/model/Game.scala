package model

import java.util.Date

final case class Game(id: String, date: Date, winner: Winner, losers: List[Loser]) {
  def pointsWon: Int = losers.map(_.points * -1).sum
}
