package dev.jtbw.adventofcode.day18

import com.github.ajalt.mordant.table.grid
import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.day18.Day18.DigStep
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.submitAnswer
import dev.jtbw.adventofcode.util.twodeespace.Direction.Diagonal
import dev.jtbw.adventofcode.util.twodeespace.Direction.Diagonal.UPLEFT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.DOWN
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.LEFT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.RIGHT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.UP
import dev.jtbw.adventofcode.util.twodeespace.Offset
import dev.jtbw.adventofcode.util.twodeespace.component
import dev.jtbw.adventofcode.util.twodeespace.isHorizontal
import dev.jtbw.adventofcode.util.twodeespace.minus
import dev.jtbw.adventofcode.util.twodeespace.offset
import dev.jtbw.adventofcode.util.twodeespace.plus
import dev.jtbw.adventofcode.util.twodeespace.rotateLeft
import dev.jtbw.adventofcode.util.twodeespace.rotateRight
import dev.jtbw.adventofcode.util.twodeespace.times
import dev.jtbw.adventofcode.util.twodeespace.toUnit
import dev.jtbw.logsugar.inspect
import dev.jtbw.logsugar.log
import dev.jtbw.logsugar.logDivider
import dev.jtbw.scriptutils.matchGroups
import dev.jtbw.scriptutils.shouldBe

fun main() = Day18.run()

object Day18 : AoCDay<List<DigStep>> {
  override val parser = Parser { lines ->
    lines.map { line ->
      line.matchGroups(Regex("""(\w) (\d+) \(#(.*)\)"""))
        .let { (dir, dist, color) ->
          val direction = when(dir) {
            "U" -> UP
            "D" -> DOWN
            "L" -> LEFT
            "R" -> RIGHT
            else -> error("")
          }
          DigStep(direction, dist.toInt(), color) }
    }
  }

  data class DigStep(
    val direction: Orthogonal,
    val distance: Int,
    val color: String
  )

  fun DigStep.fix(): DigStep {
    val distance = color.dropLast(1).toInt(16)
    val direction = when(color.last()) {
      '0' -> RIGHT
      '1' -> DOWN
      '2' -> LEFT
      '3' -> UP
      else -> error("")
    }
    return copy(distance = distance, direction = direction)
  }

  override fun example() {
    val input = parseInput("Day18ex.txt").inspect()

    // w=7
    val vertices = buildVertices(input)
    vertices.size.inspect("num verts")

    vertices.iterator().apply {
      next() shouldBe Offset(0, 0)
      next() shouldBe Offset(7, 0)
      next() shouldBe Offset(7, 6)
      next() shouldBe Offset(5, 6)
      next() shouldBe Offset(5, 7)
      next() shouldBe Offset(7, 7)
      next() shouldBe Offset(7, 10)
      next() shouldBe Offset(1, 10)
      next() shouldBe Offset(1, 8)
      next() shouldBe Offset(0, 8)
      next() shouldBe Offset(0, 5)
      next() shouldBe Offset(2, 5)
      next() shouldBe Offset(2, 3)
      next() shouldBe Offset(0, 3)

    }

    shoelaceArea(vertices)
      .inspect("shoelace")
  }

  override fun part1() {
    val input = parseInput()
    shoelaceArea(buildVertices(input))
      .toInt()
      .inspect()
      .shouldBe(74074)
  }

  override fun part2() {
    val input = parseInput()
      .map { it.fix() }
    shoelaceArea(buildVertices(input))
      .toInt()
      .inspect()
      //.shouldBe(74074)
      .also { submitAnswer(18, 2, it) }
  }

  private fun buildVertices(steps: List<DigStep>): List<Offset> {
    return buildList {
      var gridPos = Offset(0, 0)
      //var pos = Offset(0, 0)
      var corner: Diagonal = UPLEFT
      add(gridPos)

      steps.windowed(2, 1).forEach { (step, next) ->
        //logDivider(weight = 1)
        var dist = step.distance
        val vec = step.direction.offset
        val pos = gridPos
        //val (p, g) = if(step.direction.isHorizontal) { pos.x to gridPos.x } else { pos.y to gridPos.y }

        var nextCorner = corner
        val movingTowardCorner: Boolean = corner.component(step.direction) > 0
        if(next.direction == step.direction.rotateRight()) {
          //log("right turn")
          if(movingTowardCorner) {

          } else {
            dist++
            nextCorner = corner.rotateRight()
          }
        } else if(next.direction == step.direction.rotateLeft()) {
          //log("left turn")
          if(movingTowardCorner) {
            dist--
            nextCorner = corner.rotateLeft()
          } else {
          }
        } else {
          error("blah")
        }

        gridPos += vec * dist
        //log("$pos / $corner: ${step.direction to step.distance}, ${next.direction to next.distance} -> {$dist} $gridPos / $nextCorner")
        corner = nextCorner
        add(gridPos)
      }
    }
  }
}

/* Credit: https://rosettacode.org/wiki/Shoelace_formula_for_polygonal_area */
fun shoelaceArea(v: List<Offset>): Double {
  val n = v.size
  var a = 0.0
  for (i in 0..<n - 1) {
    a += v[i].x * v[i + 1].y - v[i + 1].x * v[i].y
  }
  return Math.abs(a + v[n - 1].x * v[0].y - v[0].x * v[n -1].y) / 2.0
}

