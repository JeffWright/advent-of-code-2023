package dev.jtbw.adventofcode.day8

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.leastCommonMultiple
import dev.jtbw.adventofcode.util.repeatForever
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.matchGroups
import dev.jtbw.scriptutils.shouldBe

fun main() = Day8.run()

object Day8 : AoCDay<Day8.Input> {
  data class Input(val instructions: String, val map: Map<String, Pair<String, String>>)

  override val parser = Parser { lines ->
    val instructions = lines.first()
    val map =
      lines.drop(2).associate {
        val (key, left, right) = it.matchGroups(Regex("""(\S+) = \((\S+), (\S+)\)"""))
        key to (left to right)
      }

    Input(instructions, map)
  }

  override fun part1() {
    val CORRECT = 13301
    val (instructions, map) = parseInput()

    var current = "AAA"
    val steps =
      instructions
        .asSequence()
        .repeatForever()
        .withIndex()
        .first { (_, lr) ->
          current = current.move(map, lr)
          current == "ZZZ"
        }
        .index + 1

    steps.inspect().shouldBe(CORRECT)
  }

  override fun part2() {
    val CORRECT = 7309459565207
    val (instructions, map) = parseInput()
    val startingLocations = map.keys.filter { it.endsWith("A") }

    val cycleLengths =
      startingLocations.map { start ->
        var current = start
        instructions
          .asSequence()
          .repeatForever()
          .withIndex()
          .first { (_, lr) ->
            current = current.move(map, lr)
            current.endsWith("Z")
          }
          .index + 1
      }

    cycleLengths shouldBe listOf(18961, 12169, 17263, 13301, 14999, 16697)
    val answer = cycleLengths.leastCommonMultiple()
    answer.inspect() shouldBe CORRECT
  }

  private fun String.move(map: Map<String, Pair<String, String>>, lr: Char): String {
    val src = this
    return if (lr == 'L') {
      map[src]!!.first
    } else {
      map[src]!!.second
    }
  }
}
