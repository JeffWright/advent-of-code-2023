package dev.jtbw.adventofcode.day11

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.day11.Space.Galaxy
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.ascendingRangeTo
import dev.jtbw.adventofcode.util.twodeespace.Grid
import dev.jtbw.adventofcode.util.twodeespace.Offset
import dev.jtbw.adventofcode.util.twodeespace.forEachWithOffset
import dev.jtbw.adventofcode.util.twodeespace.toGrid
import dev.jtbw.adventofcode.util.twodeespace.toMultilineString
import dev.jtbw.adventofcode.util.twodeespace.width
import dev.jtbw.logsugar.inspect
import dev.jtbw.logsugar.runTiming
import dev.jtbw.scriptutils.shouldBe
import kotlin.math.abs

fun main() = Day11.run()

enum class Space(val char: Char) {
  Galaxy('#'),
  Empty('.')
}

object Day11 : AoCDay<Grid<Space>> {
  override val parser = Parser { lines ->
    lines.toGrid { char -> Space.entries.first { it.char == char } }
  }

  override fun part1() {
    solve("Day11.txt", expansionFactor = 2).inspect() shouldBe 10173804L
  }

  override fun part2() {
    solve("Day11ex.txt", expansionFactor = 2) shouldBe 374L
    solve("Day11ex.txt", expansionFactor = 10) shouldBe 1030L
    solve("Day11ex.txt", expansionFactor = 100) shouldBe 8410L
    runTiming("solving part 2") { solve("Day11.txt", expansionFactor = 1_000_000) }
      .inspect() shouldBe 634324905172L
  }

  private fun solve(filename: String, expansionFactor: Long): Long {
    val grid = parseInput(filename)
    grid.toMultilineString { it.char.toString() }

    val emptyColumns = (0..grid.width).toMutableSet()
    val emptyRows = (0..grid.width).toMutableSet()
    val galaxies = mutableSetOf<Offset>()
    grid.forEachWithOffset { pos, space ->
      if (space == Galaxy) {
        galaxies += pos
        emptyColumns.remove(pos.x)
        emptyRows.remove(pos.y)
      }
    }

    val galaxyList = galaxies.toList()
    return galaxyList.withIndex().sumOf { (idx, galaxy) ->
      galaxyList.slice(idx + 1..galaxyList.lastIndex).sumOf { other ->
        distance(galaxy, other, emptyColumns, emptyRows, expansionFactor)
      }
    }
  }

  /** @param expansionFactor 1 empty row becomes this many pseudo-rows (e.g. '2' for part 1) */
  private fun distance(
    from: Offset,
    to: Offset,
    emptyColumns: Set<Int>,
    emptyRows: Set<Int>,
    expansionFactor: Long
  ): Long {
    val euclidean = abs(to.x - from.x) + abs(to.y - from.y).toLong()
    val xRange = from.x.ascendingRangeTo(to.x)
    val yRange = from.y.ascendingRangeTo(to.y)
    val extraDistanceFromColumns = emptyColumns.count { it in xRange } * (expansionFactor - 1)
    val extraDistanceFromRows = emptyRows.count { it in yRange } * (expansionFactor - 1)

    return euclidean + extraDistanceFromColumns + extraDistanceFromRows
  }
}
