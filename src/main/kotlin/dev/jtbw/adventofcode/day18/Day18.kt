package dev.jtbw.adventofcode.day18

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.day18.Day18.DigStep
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.shoelaceArea
import dev.jtbw.adventofcode.util.twodeespace.Direction.Diagonal
import dev.jtbw.adventofcode.util.twodeespace.Direction.Diagonal.DOWNLEFT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Diagonal.DOWNRIGHT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Diagonal.UPLEFT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Diagonal.UPRIGHT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.DOWN
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.LEFT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.RIGHT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.UP
import dev.jtbw.adventofcode.util.twodeespace.Offset
import dev.jtbw.adventofcode.util.twodeespace.component
import dev.jtbw.adventofcode.util.twodeespace.offset
import dev.jtbw.adventofcode.util.twodeespace.plus
import dev.jtbw.adventofcode.util.twodeespace.rotateLeft
import dev.jtbw.adventofcode.util.twodeespace.rotateRight
import dev.jtbw.adventofcode.util.twodeespace.times
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.matchGroups
import dev.jtbw.scriptutils.shouldBe

fun main() = Day18.run()

object Day18 : AoCDay<List<DigStep>> {
  override val parser = Parser { lines ->
    lines.map { line ->
      line.matchGroups(Regex("""(\w) (\d+) \(#(.*)\)""")).let { (dir, dist, color) ->
        val direction =
          when (dir) {
            "U" -> UP
            "D" -> DOWN
            "L" -> LEFT
            "R" -> RIGHT
            else -> error("")
          }
        DigStep(direction, dist.toInt(), color)
      }
    }
  }

  data class DigStep(val direction: Orthogonal, val distance: Int, val color: String)

  fun DigStep.fix(): DigStep {
    val distance = color.dropLast(1).toInt(16)
    val direction =
      when (color.last()) {
        '0' -> RIGHT
        '1' -> DOWN
        '2' -> LEFT
        '3' -> UP
        else -> error("")
      }
    return copy(distance = distance, direction = direction)
  }

  override fun part1() {
    val input = parseInput()

    shoelaceArea(buildVertices(input)).toInt().inspect().shouldBe(74074)
  }

  override fun part2() {
    val input = parseInput().map { it.fix() }

    shoelaceArea(buildVertices(input)).toLong().inspect().shouldBe(112074045986829)
  }

  /** This makes the assumption that the overall turn is right-handed, but my input is so *shrug* */
  private fun buildVertices(steps: List<DigStep>): List<Offset> {
    return buildList {
      var pos = Offset(0, 0)
      add(pos)

      var corner: Diagonal =
        when (steps.first().direction) {
          RIGHT -> UPLEFT
          DOWN -> UPRIGHT
          LEFT -> DOWNRIGHT
          UP -> DOWNLEFT
        }

      (steps + steps.first()).windowed(2, 1).forEach { (step, next) ->
        var dist = step.distance
        val vec = step.direction.offset

        var nextCorner = corner
        val movingTowardCorner: Boolean = corner.component(step.direction) > 0
        if (next.direction == step.direction.rotateRight()) {
          // Right turn
          if (!movingTowardCorner) {
            dist++
            nextCorner = corner.rotateRight()
          }
        } else if (next.direction == step.direction.rotateLeft()) {
          // Left turn
          if (movingTowardCorner) {
            dist--
            nextCorner = corner.rotateLeft()
          }
        } else {
          error("blah")
        }

        pos += vec * dist
        corner = nextCorner
        add(pos)
      }
    }
  }
}
