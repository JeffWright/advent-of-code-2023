package dev.jtbw.adventofcode.day17

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.SearchStrategy.DEPTH_FIRST
import dev.jtbw.adventofcode.util.traverse
import dev.jtbw.adventofcode.util.twodeespace.Direction.Companion.orthogonals
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.UP
import dev.jtbw.adventofcode.util.twodeespace.Grid
import dev.jtbw.adventofcode.util.twodeespace.Offset
import dev.jtbw.adventofcode.util.twodeespace.get
import dev.jtbw.adventofcode.util.twodeespace.height
import dev.jtbw.adventofcode.util.twodeespace.inBounds
import dev.jtbw.adventofcode.util.twodeespace.opposite
import dev.jtbw.adventofcode.util.twodeespace.plus
import dev.jtbw.adventofcode.util.twodeespace.width
import dev.jtbw.logsugar.log
import dev.jtbw.scriptutils.shouldBe

fun main() = Day17.run()

object Day17 : AoCDay<Grid<Int>> {
  override val parser = Parser { lines -> lines.map { it.map { it.digitToInt() } } }

  data class Node(val offset: Offset, val direction: Orthogonal, val dirCount: Int)

  override fun part1() {
    solve(0, 3).shouldBe(698)
  }

  override fun part2() {
    solve(4, 10).shouldBe(825)
  }

  private fun solve(min: Int, max: Int): Int {
    val grid = parseInput()

    val start = Node(Offset(0, 0), UP, 0)
    val end = Offset(grid.width - 1, grid.height - 1)

    var finalDistance: Int? = null

    traverse(strategy = DEPTH_FIRST, start = start to 0, nodeIdentity = { it.first }) {
      (node, distanceSoFar) ->
      if (node.offset == end && node.dirCount in min..max) {
        log("Found ${grid[node.offset]}: dist = $distanceSoFar")
        finalDistance = distanceSoFar
        shortCircuit()
      }

      val allowedDirs =
        when (node.dirCount) {
          // the first move, can be anything
          0 -> orthogonals
          // if you just started moving, can't turn
          in 1 ..< min -> listOf(node.direction)
          // Can go straight, left, or right
          in min ..< max -> orthogonals - node.direction.opposite
          // Can only go left or right
          else -> orthogonals - node.direction - node.direction.opposite
        }

      allowedDirs.forEach { dir ->
        val newOffset = node.offset + dir

        if (grid.inBounds(newOffset)) {
          val nextNode =
            Node(
              offset = newOffset,
              direction = dir,
              dirCount = if (dir == node.direction) node.dirCount + 1 else 1
            )
          val nextDistance = (distanceSoFar) + grid[newOffset]
          search(node = nextNode to nextDistance, priority = nextDistance)
        }
      }
    }

    return finalDistance!!
  }
}
