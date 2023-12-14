package dev.jtbw.adventofcode.day14

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.DOWN
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.LEFT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.RIGHT
import dev.jtbw.adventofcode.util.twodeespace.Direction.Orthogonal.UP
import dev.jtbw.adventofcode.util.twodeespace.Grid
import dev.jtbw.adventofcode.util.twodeespace.MutableGrid
import dev.jtbw.adventofcode.util.twodeespace.Offset
import dev.jtbw.adventofcode.util.twodeespace.asSequenceWithOffset
import dev.jtbw.adventofcode.util.twodeespace.directionTo
import dev.jtbw.adventofcode.util.twodeespace.get
import dev.jtbw.adventofcode.util.twodeespace.height
import dev.jtbw.adventofcode.util.twodeespace.isHorizontal
import dev.jtbw.adventofcode.util.twodeespace.plus
import dev.jtbw.adventofcode.util.twodeespace.set
import dev.jtbw.adventofcode.util.twodeespace.toGrid
import dev.jtbw.adventofcode.util.twodeespace.toMutableGrid
import dev.jtbw.adventofcode.util.twodeespace.width
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.shouldBe

fun main() = Day14.run()

object Day14 : AoCDay<Grid<Char>> {
  override val parser = Parser { lines -> lines.toGrid { it } }

  override fun part1() {
    val input = parseInput().toMutableGrid()

    input.tilt(UP)
    input.totalLoad().inspect() shouldBe 108813
  }

  override fun part2() {
    val input = parseInput().toMutableGrid()

    val numCycles = 1_000_000_000

    val loads =
      sequence {
          repeat(numCycles) {
            (listOf(UP, LEFT, DOWN, RIGHT)).forEach { dir -> input.tilt(dir) }
            yield(input.totalLoad())
          }
        }
        .withIndex()
        .iterator()

    val sample = mutableListOf<IndexedValue<Int>>()

    // Detect a cycle of length at least 5
    val cycleLength: Int
    while (true) {
      val cycle = sample.asReversed().map { it.value }.detectCycle()
      if (cycle != null && cycle > 5) {
        cycleLength = cycle
        break
      } else {
        sample += loads.next()
      }
    }

    val last = sample.last().index

    val addl = (numCycles - 1 - last).rem(cycleLength)

    sample[last + addl - cycleLength].value.inspect("answer").shouldBe(104533)
  }

  private fun MutableGrid<Char>.tilt(towards: Orthogonal) {
    val grid = this

    val iterationOrder = sequence {
      // iterates against rolling direction
      val againstRoll =
        when (towards) {
          DOWN -> (height - 1).downTo(0)
          LEFT -> 0 ..< width
          RIGHT -> (width - 1).downTo(0)
          UP -> 0 ..< height
        }

      // iterates perpendicular to rolling direction
      val acrossRoll =
        when {
          towards.isHorizontal -> 0 ..< height
          else -> 0 ..< width
        }

      acrossRoll.forEach { across ->
        againstRoll.forEach { against ->
          yield(
            if (towards.isHorizontal) {
              Offset(against, across)
            } else {
              Offset(across, against)
            }
          )
        }
      }
    }

    var nextOpen: Offset? = null
    iterationOrder.forEach { offset ->
      val newRow =
        when (towards) {
          DOWN -> offset.y == height - 1
          LEFT -> offset.x == 0
          RIGHT -> offset.x == width - 1
          UP -> offset.y == 0
        }
      if (newRow) {
        nextOpen = null
      }

      val next = nextOpen
      val c = grid[offset]
      when (c) {
        '.' -> {
          if (next == null) {
            nextOpen = offset
          }
        }
        '#' -> nextOpen = null
        'O' -> {
          if (next != null) {
            grid[offset] = '.'
            grid[next] = 'O'
            val dir = next.directionTo(offset)
            nextOpen = next + dir
          }
        }
      }
    }
  }

  private fun Grid<Char>.totalLoad(): Int {
    return asSequenceWithOffset().sumOf { (offset, c) ->
      if (c == 'O') {
        // y is top-bottom
        height - offset.y
      } else {
        0
      }
    }
  }
}

data class Cycle(val length: Int, val count: Int)

private fun <T> List<T>.detectCycle(minCount: Int = 2): Int? {
  val list = this
  val length =
    (size / 2).downTo(1).firstOrNull { len -> (0..len).all { list[it] == list[it + len] } }
      ?: return null

  return length.takeIf {
    val c = slice(0 ..< length)
    (1 ..< minCount).all { repetition ->
      val c2 = slice(repetition * length ..< (repetition + 1) * length)
      c == c2
    }
  }
}
