package dao

import java.util.Date

import model.LossType.{FoldDouble, FoldSingle, Unfinished}
import model.WinType.{JokersWin, PairsWin, StraightWin}
import model.{Game, Loser, Player, Winner}

class GameRepo extends RepoTrait {

  private val amy = Player("amy", "amy")
  private val bob = Player("bob", "bob")
  private val david = Player("david", "david")
  private val elmo = Player("elmo", "elmo")

  private var players = Set(amy, bob, david, elmo)

  private val games = Set(
    Game("1", new Date, Winner(amy, StraightWin),
      Loser(bob, Unfinished, -10) ::
        Loser(david, FoldSingle, -20) ::
        Loser(elmo, Unfinished, -50) ::
        Nil),
    Game("2", new Date, Winner(bob, PairsWin),
      Loser(amy, Unfinished, -10) ::
        Loser(david, FoldSingle, -10) ::
        Loser(elmo, FoldSingle, -10) ::
        Nil),
    Game("3", new Date, Winner(amy, StraightWin),
      Loser(bob, Unfinished, -20) ::
        Loser(david, FoldDouble, -10) ::
        Loser(elmo, Unfinished, -30) ::
        Nil),
    Game("4", new Date, Winner(david, JokersWin),
      Loser(bob, FoldDouble, -30) ::
        Loser(amy, FoldDouble, -30) ::
        Loser(elmo, Unfinished, -10) ::
        Nil),
    Game("5", new Date, Winner(elmo, StraightWin),
      Loser(bob, Unfinished, -50) ::
        Loser(david, Unfinished, -50) ::
        Loser(amy, Unfinished, -10) ::
        Nil)
  )

  override def getGame(id: String): Option[Game] = games find (_.id == id)

  override def getAllGames: Set[Game] = games

  def getAllPlayers: Set[Player] = players

  def addPlayer(id: String, name: String) = {
    players = players + Player(id, name)
    true
  }
}
