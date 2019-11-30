package model

sealed trait WinType
object WinType {
  case object StraightWin extends WinType
  case object PairsWin extends WinType
  case object JokersWin extends WinType
}