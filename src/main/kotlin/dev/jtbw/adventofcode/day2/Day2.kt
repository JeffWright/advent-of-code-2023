package dev.jtbw.adventofcode.day2

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.match
import dev.jtbw.scriptutils.matchOrNull
import dev.jtbw.scriptutils.shouldBe

fun main() {
  Day2.run()
}

object Day2 : AoCDay<List<Game>> {
  override val parser = Parser { lines ->
    lines.map {
      Game(
          id = it.substringBefore(":").match(Regex("""(\d+)""")).toInt(),
          plays = it.substringAfter(":").toPlays())
    }
  }

  override fun part1() {
    val CORRECT = 2256
    parseInput().filter { it.isValid() }.sumOf { it.id }.inspect() shouldBe CORRECT
  }

  override fun part2() {
    val CORRECT = 74229
    parseInput().sumOf { it.power() }.inspect() shouldBe CORRECT
  }
}

fun String.toPlays(): List<Play> {
  val reGreen = Regex("""(\d+) green""")
  val reBlue = Regex("""(\d+) blue""")
  val reRed = Regex("""(\d+) red""")
  return split(";").map {
    Play(
        green = it.matchOrNull(reGreen)?.toInt() ?: 0,
        blue = it.matchOrNull(reBlue)?.toInt() ?: 0,
        red = it.matchOrNull(reRed)?.toInt() ?: 0,
    )
  }
}

fun Game.isValid(): Boolean {
  // 12 red cubes, 13 green cubes, and 14 blue cubes
  return plays.all { it.red <= 12 && it.green <= 13 && it.blue <= 14 }
}

fun Game.power(): Int {
  val red = plays.maxOf { it.red }
  val green = plays.maxOf { it.green }
  val blue = plays.maxOf { it.blue }

  return red * green * blue
}

data class Game(val id: Int, val plays: List<Play>)

data class Play(val green: Int, val blue: Int, val red: Int)
