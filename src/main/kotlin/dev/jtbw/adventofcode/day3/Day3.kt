package dev.jtbw.adventofcode.day3

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.Direction
import dev.jtbw.adventofcode.util.Offset
import dev.jtbw.adventofcode.util.get
import dev.jtbw.adventofcode.util.inBounds
import dev.jtbw.adventofcode.util.offset
import dev.jtbw.adventofcode.util.plus
import dev.jtbw.adventofcode.util.set
import dev.jtbw.logsugar.inspect
import dev.jtbw.logsugar.log
import dev.jtbw.scriptutils.shouldBe

fun main() = Day3.run()

object Day3 : AoCDay<GridWithMeta> {
  override val parser = Parser { lines ->
    val grid: Grid = lines.map { it.toCharArray().toList() }
    GridWithMeta(
        grid = grid,
        consumed = (0..(grid.height)).map { (0..grid.width).map { false }.toMutableList() })
  }

  override fun part1() {
    val CORRECT = 546312
    val grid = parseInput()

    var sum = 0
    for (x in 0 ..< grid.grid.width) {
      for (y in 0 ..< grid.grid.height) {
        if (grid.grid[x, y].isSymbol) {
          println("symbol @ $x, $y")
          Direction.all.forEach {
            println(it)
            val offset = Offset(x, y) + it.offset
            sum += grid.consume(offset.x, offset.y).inspect("from $offset") ?: 0
          }
        }
      }
    }

    grid.consumed.forEach { println(it.map { if (it) 'C' else '.' }.joinToString("")) }

    println("sum = $sum")
    sum shouldBe CORRECT
  }

  override fun part2() {
    val CORRECT = 87449461
    // test()
    val grid = parseInput()

    var sum = 0
    val parts = mutableListOf<Int?>()
    for (x in 0 ..< grid.grid.width) {
      for (y in 0 ..< grid.grid.height) {
        if (grid.grid[x, y] == '*') {
          println("gear @ $x, $y")
          Direction.all.forEach {
            // println(it)
            val offset = Offset(x, y) + it.offset
            parts += grid.consume(offset.x, offset.y)
            // .inspect("from $offset")
          }
          sum +=
              parts.filterNotNull().let {
                if (it.size == 2) {
                  log("is gear! ${it[0]} * ${it[1]}")
                  it[0] * it[1]
                } else {
                  log("not gear (${it.size})")
                  0
                }
              }
          grid.consumed.reset()
          parts.clear()
        }
      }
    }

    println("sum = $sum")
    sum shouldBe CORRECT
  }

  override fun tests() {
    val gridWithMeta = parseInput()
    with(gridWithMeta) {
      grid[10, 0].shouldBe('2')
      grid[8, 0].shouldBe('.')
      grid[12, 0].shouldBe('.')
      grid.inBounds(5, 5).shouldBe(true)
      grid.inBounds(5, 500).shouldBe(false)
      grid.inBounds(5, 140).shouldBe(false)
    }

    gridWithMeta.consume(10, 0).shouldBe(426)
    gridWithMeta.consume(11, 0).shouldBe(null)
  }
}

private fun List<MutableList<Boolean>>.reset() {
  forEach { row -> row.indices.forEach { row[it] = false } }
}

typealias Grid = List<List<Char>>

data class GridWithMeta(
    val grid: Grid,
    val consumed: List<MutableList<Boolean>>,
)

private val Char.isSymbol: Boolean
  get() {
    return this != '.' && !this.isDigit()
  }
val Grid.width
  get() = this[0].size
val Grid.height
  get() = this.size

fun GridWithMeta.consume(x: Int, y: Int): Int? {
  // log("consume $x, $y")

  if (!grid.inBounds(x, y)) {
    return null
  }

  if (consumed[x, y]) {
    return null
  }

  if (!grid[x, y].isDigit()) {
    return null
  }

  val start =
      x.downTo(Int.MIN_VALUE).first { x ->
        // grid[x, y].inspect("$x, $y")
        !grid.inBounds(x, y) || !grid[x, y].isDigit()
      } + 1

  val end = (x..Int.MAX_VALUE).first { x -> !grid.inBounds(x, y) || !grid[x, y].isDigit() } - 1

  (start..end).forEach { consumed[it, y] = true }
  return grid[y].subList(start, end + 1).joinToString("").toInt()
}
