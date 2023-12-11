package dev.jtbw.adventofcode.util

import dev.jtbw.adventofcode.util.Direction.Diagonal.*
import dev.jtbw.adventofcode.util.Direction.Orthogonal.*

sealed interface Direction {
  sealed interface Orthogonal : Direction {
    data object UP : Orthogonal
    data object DOWN : Orthogonal
    data object LEFT : Orthogonal
    data object RIGHT : Orthogonal
  }

  sealed interface Diagonal : Direction {
    data object UPLEFT : Diagonal
    data object UPRIGHT : Diagonal
    data object DOWNLEFT : Diagonal
    data object DOWNRIGHT : Diagonal
  }

  companion object {
    val orthogonals = listOf(UP, DOWN, LEFT, RIGHT)
    val diagonals = listOf(UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT)
    val all = orthogonals + diagonals
  }
}

val Direction.Orthogonal.opposite: Direction.Orthogonal
  get() =
      when (this) {
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
        UP -> DOWN
      }

data class Offset(val x: Int, val y: Int) {
  override fun toString(): String {
    return "($x, $y)"
  }
}

operator fun Offset.plus(other: Offset): Offset {
  return Offset(x + other.x, y + other.y)
}

operator fun Offset.minus(other: Offset): Offset {
  return Offset(x - other.x, y - other.y)
}

val Direction.offset: Offset
  get() {
    return when (this) {
      DOWN -> Offset(0, 1)
      DOWNLEFT -> Offset(-1, 1)
      DOWNRIGHT -> Offset(1, 1)
      LEFT -> Offset(-1, 0)
      RIGHT -> Offset(1, 0)
      UP -> Offset(0, -1)
      UPLEFT -> Offset(-1, -1)
      UPRIGHT -> Offset(1, -1)
    }
  }

operator fun Offset.plus(direction: Direction) = plus(direction.offset)

fun Offset.toOrthogonal() = Direction.orthogonals.first { it.offset == this }

fun Offset.toDirection() = Direction.all.first { it.offset == this }

fun Direction.Orthogonal.rotateLeft() =
    when (this) {
      UP -> LEFT
      LEFT -> DOWN
      DOWN -> RIGHT
      RIGHT -> UP
    }

fun Direction.Orthogonal.rotateRight() =
    when (this) {
      UP -> RIGHT
      RIGHT -> DOWN
      DOWN -> LEFT
      LEFT -> UP
    }

fun Direction.Orthogonal.rotate90(clockwise: Boolean) =
    if (clockwise) rotateRight() else rotateLeft()
