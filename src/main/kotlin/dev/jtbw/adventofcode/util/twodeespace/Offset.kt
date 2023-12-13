package dev.jtbw.adventofcode.util.twodeespace

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

operator fun Offset.plus(direction: Direction) = plus(direction.offset)

fun Offset.toOrthogonal() = Direction.orthogonals.first { it.offset == this }

fun Offset.toDirection() = Direction.all.first { it.offset == this }
