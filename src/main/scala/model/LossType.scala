package model

sealed trait LossType
object LossType {
  case object Unfinished extends LossType
  case object FoldSingle extends LossType
  case object FoldDouble extends LossType
}
