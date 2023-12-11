package dev.jtbw.adventofcode.day10

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.day10.Space.*
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.twodeespace.Direction.Companion.orthogonals
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.*
import dev.jtbw.adventofcode.util.twodeespace.Offset
import dev.jtbw.adventofcode.util.SearchStrategy.BREADTH_FIRST
import dev.jtbw.adventofcode.util.SearchStrategy.DEPTH_FIRST
import dev.jtbw.adventofcode.util.ShouldContinue.CONTINUE
import dev.jtbw.adventofcode.util.graphSearch
import dev.jtbw.adventofcode.util.twodeespace.Grid
import dev.jtbw.adventofcode.util.twodeespace.get
import dev.jtbw.adventofcode.util.twodeespace.inBounds
import dev.jtbw.adventofcode.util.twodeespace.minus
import dev.jtbw.adventofcode.util.twodeespace.opposite
import dev.jtbw.adventofcode.util.twodeespace.plus
import dev.jtbw.adventofcode.util.twodeespace.rotate90
import dev.jtbw.adventofcode.util.twodeespace.rotateLeft
import dev.jtbw.adventofcode.util.twodeespace.rotateRight
import dev.jtbw.adventofcode.util.twodeespace.toOrthogonal
import dev.jtbw.logsugar.ANSI_BRIGHT_BLUE
import dev.jtbw.logsugar.ANSI_BRIGHT_GREEN
import dev.jtbw.logsugar.colorized
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.shouldBe

fun main() = Day10.run()

sealed interface Space {

  data class Pipe(val dirs: Pair<Orthogonal, Orthogonal>) : Space

  data object Start : Space

  data object Blank : Space
}

object Day10 : AoCDay<Grid<Space>> {
  override val parser = Parser { lines -> lines.map { line -> line.map { it.toSpace() } } }

  override fun part1() {
    // Covered by part2
  }

  override fun part2() {
    val grid = parseInput()

    val loop = findLoop(grid)
    val enclosed = mutableListOf<Offset>()

    val isClockwise = isClockwise(loop)
    val innerBorder = getInnerBorder(loop, isClockwise)

    graphSearch(
        strategy = BREADTH_FIRST,
        starts = innerBorder,
        getNextNodes = { pos ->
          orthogonals.map { pos + it }.filter { grid.inBounds(it) }.filter { it !in loop }
        },
        onVisit = { pos, _ ->
          enclosed += pos
          CONTINUE
        })

    prettyPrint(grid, loop.toSet(), enclosed.toSet())

    val farthest = loop.size / 2
    farthest.inspect("farthest point (part 1 answer)") shouldBe 6886
    enclosed.size.inspect("number of enclosed spaces (part 2 answer)") shouldBe 371
  }

  private fun findStartingPosition(grid: Grid<Space>): Offset {
    grid.forEachIndexed { y, rows ->
      rows.forEachIndexed { x, space ->
        if (space == Start) {
          return Offset(x, y)
        }
      }
    }
    error("No start found")
  }

  private fun findLoop(grid: Grid<Space>): MutableList<Offset> {
    val loop = mutableListOf<Offset>()
    val start = findStartingPosition(grid).inspect("Start @")
    graphSearch(
        strategy = DEPTH_FIRST,
        starts = listOf(start),
        getNextNodes = { pos ->
          when (val space = grid[pos]) {
            is Pipe -> listOf(pos + space.dirs.first, pos + space.dirs.second)
            is Start -> {
              orthogonals.mapNotNull { dir ->
                if (hasExit(grid, pos + dir, dir.opposite)) {
                  (pos + dir)
                } else {
                  null
                }
              }
            }
            is Blank -> emptyList()
          }.filter { grid.inBounds(it) }
        },
        onVisit = { pos, _ ->
          loop += pos
          CONTINUE
        },
    )
    return loop
  }

  private fun hasExit(grid: Grid<Space>, pos: Offset, dir: Orthogonal): Boolean {
    val pipe = grid[pos]
    return (pipe is Pipe && (pipe.dirs.first == dir || pipe.dirs.second == dir))
  }

  /** return all positions that are immediately "to the right of" loop (or to the left, if ccw) */
  private fun getInnerBorder(loop: List<Offset>, isClockwise: Boolean): List<Offset> {
    return loop
        .windowed(3, 1) { (a, b, c) ->
          // centered on b
          val incomingDir = (b - a).toOrthogonal()
          val outgoingDir = (c - b).toOrthogonal()

          val dirs =
              when {
                incomingDir == outgoingDir -> listOf(incomingDir.rotate90(isClockwise))
                incomingDir.rotate90(!isClockwise) == outgoingDir ->
                    listOf(incomingDir, incomingDir.rotate90(isClockwise))
                else -> emptyList()
              }

          dirs.map { b + it }
        }
        .flatten()
        .filterNot { it in loop }
  }

  private fun isClockwise(path: List<Offset>): Boolean {
    var lefts = 0
    var rights = 0
    (path + path[path.lastIndex - 1] + path[path.lastIndex]).windowed(3, 1).forEach { (a, b, c) ->
      val ab = (b - a).toOrthogonal()
      val bc = (c - b).toOrthogonal()
      if (ab.rotateLeft() == bc) {
        lefts++
      } else if (ab.rotateRight() == bc) {
        rights++
      }
    }
    return rights > lefts
  }
}

fun Char.toSpace(): Space {
  return when (this) {
    '|' -> Pipe(UP to DOWN)
    '-' -> Pipe(LEFT to RIGHT)
    'L' -> Pipe(RIGHT to UP)
    'J' -> Pipe(LEFT to UP)
    '7' -> Pipe(LEFT to DOWN)
    'F' -> Pipe(RIGHT to DOWN)
    'S' -> Start
    else -> Blank
  }
}

fun Space.pretty(): String {
  return when (this) {
    Pipe(UP to DOWN) -> " ┃ "
    Pipe(LEFT to RIGHT) -> "━━━"
    Pipe(RIGHT to UP) -> " ┗━"
    Pipe(LEFT to UP) -> "━┛ "
    Pipe(LEFT to DOWN) -> "━┓ "
    Pipe(RIGHT to DOWN) -> " ┏━"
    Start -> "STR"
    else -> " . "
  }
}

fun prettyPrint(grid: Grid<Space>, loop: Set<Offset>, enclosed: Set<Offset>) {
  val prettyGrid =
      grid.mapIndexed { y, row ->
        row.mapIndexed { x, space ->
          if (Offset(x, y) in enclosed) {
            space.pretty().colorized(ANSI_BRIGHT_BLUE)
          } else if (Offset(x, y) in loop) {
            space.pretty().colorized(ANSI_BRIGHT_GREEN)
          } else {
            "   "
          }
        }
      }
  prettyGrid.forEach { row -> println(row.joinToString("")) }
}
