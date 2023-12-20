package dev.jtbw.adventofcode.util.twodeespace

import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.DOWN
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.LEFT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.RIGHT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.UP

data class Offset(val x: Long, val y: Long) {
  constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())

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

operator fun Offset.times(other: Int): Offset {
  return Offset(x * other, y * other)
}

operator fun Offset.plus(direction: Direction) = plus(direction.offset)

fun Offset.toOrthogonal() = Direction.orthogonals.first { it.offset == this }

fun Offset.toDirection() = Direction.all.first { it.offset == this }

fun Offset.toUnit() =
  Offset(
    x =
      when {
        x > 0 -> 1
        x < 0 -> -1
        else -> 0
      },
    y =
      when {
        y > 0 -> 1
        y < 0 -> -1
        else -> 0
      }
  )

fun Offset.directionTo(other: Offset): Direction {
  when {
    this.x == other.x -> {
      if (this.y > other.y) {
        return UP
      } else if (this.y < other.y) {
        return DOWN
      }
    }
    this.y == other.y -> {
      if (this.x > other.x) {
        return LEFT
      } else if (this.x < other.x) {
        return RIGHT
      }
    }
  }
  error("Cannot get direction from $this to $other")
}
