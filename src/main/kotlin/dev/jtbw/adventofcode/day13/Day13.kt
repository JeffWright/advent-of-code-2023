package dev.jtbw.adventofcode.day13

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.twodeespace.Grid
import dev.jtbw.adventofcode.util.twodeespace.get
import dev.jtbw.adventofcode.util.twodeespace.height
import dev.jtbw.adventofcode.util.twodeespace.toGrid
import dev.jtbw.adventofcode.util.twodeespace.width
import dev.jtbw.scriptutils.shouldBe
import dev.jtbw.scriptutils.split

fun main() = Day13.run()

object Day13 : AoCDay<List<Grid<Char>>> {
  override val parser = Parser { lines -> lines.split { it.isBlank() }.map { it.toGrid { it } } }

  override fun part1() {
    val input = parseInput()
    (input.sumOf { it.verticalReflectionLine(0) ?: 0 } +
        input.sumOf { (it.horizontalReflectionLine(0) ?: 0) * 100 })
      .shouldBe(30802)
  }

  override fun part2() {
    val input = parseInput()
    (input.sumOf { it.verticalReflectionLine(1) ?: 0 } +
        input.sumOf { (it.horizontalReflectionLine(1) ?: 0) * 100 })
      .shouldBe(37876)
  }

  private fun Grid<Char>.verticalReflectionLine(numConflicts: Int): Int? {
    return (0 ..< width - 1)
      .firstOrNull { column ->
        (0 ..< height).sumOf { row ->
          val fn: ((Int) -> Char) = { x -> this[x, row] }
          fn.reflectionConflicts(column, width)
        } == numConflicts
      }
      ?.plus(1)
  }

  private fun Grid<Char>.horizontalReflectionLine(numConflicts: Int): Int? {
    return (0 ..< height - 1)
      .firstOrNull { row ->
        (0 ..< width).sumOf { column ->
          val fn: ((Int) -> Char) = { y -> this[column, y] }
          fn.reflectionConflicts(row, height)
        } == numConflicts
      }
      ?.plus(1)
  }

  private fun ((Int) -> Char).reflectionConflicts(idxToLeft: Int, size: Int): Int {
    val width = minOf(idxToLeft + 1, size - idxToLeft - 1)
    return (0 ..< width).count { idx -> this(idxToLeft - idx) != this(idxToLeft + idx + 1) }
  }
}
