package dev.jtbw.adventofcode.day16

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.SearchStrategy.BREADTH_FIRST
import dev.jtbw.adventofcode.util.traverse
import dev.jtbw.adventofcode.util.twodeespace.Axis.HORIZONTAL
import dev.jtbw.adventofcode.util.twodeespace.Axis.VERTICAL
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.DOWN
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.LEFT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.RIGHT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.UP
import dev.jtbw.adventofcode.util.twodeespace.Grid
import dev.jtbw.adventofcode.util.twodeespace.Offset
import dev.jtbw.adventofcode.util.twodeespace.axis
import dev.jtbw.adventofcode.util.twodeespace.get
import dev.jtbw.adventofcode.util.twodeespace.height
import dev.jtbw.adventofcode.util.twodeespace.inBounds
import dev.jtbw.adventofcode.util.twodeespace.plus
import dev.jtbw.adventofcode.util.twodeespace.toGrid
import dev.jtbw.adventofcode.util.twodeespace.width
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.shouldBe

fun main() = Day16.run()

object Day16 : AoCDay<Grid<Char>> {
  override val parser = Parser { lines -> lines.toGrid { it } }

  override fun part1() {
    val grid = parseInput()
    countEnergized(grid, Offset(0, 0) to RIGHT).shouldBe(7632)
  }

  // TODO JTW could be optimized, but loops are tricky and I'm behind...
  override fun part2() {
    val grid = parseInput()

    sequence {
        yieldAll((0 ..< grid.width).map { Offset(it, 0) to DOWN })
        yieldAll((0 ..< grid.width).map { Offset(it, grid.height - 1) to UP })
        yieldAll((0 ..< grid.height).map { Offset(0, it) to RIGHT })
        yieldAll((0 ..< grid.height).map { Offset(grid.width - 1, it) to LEFT })
      }
      .maxOf { countEnergized(grid, it) }
      .inspect("answer")
      .shouldBe(8023)
  }

  private fun bounce(dir: Orthogonal, char: Char): List<Orthogonal> {
    val axis = dir.axis
    return sequence {
        when (char) {
          '/' ->
            when (dir) {
              DOWN -> yield(LEFT)
              LEFT -> yield(DOWN)
              RIGHT -> yield(UP)
              UP -> yield(RIGHT)
            }
          '\\' ->
            when (dir) {
              DOWN -> yield(RIGHT)
              LEFT -> yield(UP)
              RIGHT -> yield(DOWN)
              UP -> yield(LEFT)
            }
          '|' ->
            if (axis == HORIZONTAL) {
              yield(UP)
              yield(DOWN)
            } else {
              yield(dir)
            }
          '-' ->
            if (axis == VERTICAL) {
              yield(RIGHT)
              yield(LEFT)
            } else {
              yield(dir)
            }
          else -> yield(dir)
        }
      }
      .toList()
  }

  /** node, if followed, lights up this many tiles */
  fun countEnergized(grid: Grid<Char>, initial: Pair<Offset, Orthogonal>): Int {
    val energized = mutableSetOf<Offset>()

    traverse(
      strategy = BREADTH_FIRST,
      start = initial,
    ) { (offset, dir) ->
      energized.add(offset)
      val c = grid[offset]
      bounce(dir, c).forEach { newDir ->
        if (grid.inBounds(offset + newDir)) {
          search(offset + newDir to newDir)
        }
      }
    }

    return energized.count()
  }
}
