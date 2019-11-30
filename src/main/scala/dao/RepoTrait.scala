package dao

import model.Game

trait RepoTrait {
  def getGame(id: String): Option[Game]

  def getAllGames: Set[Game]
}
