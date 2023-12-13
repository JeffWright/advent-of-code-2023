package dev.jtbw.adventofcode.day6

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.shouldBe
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt

fun main() = Day6.run()

object Day6 : AoCDay<List<Day6.Race>> {
  data class Race(val duration: Long, val record: Long)

  override val parser = Parser { lines ->
    val (timesLine, distanceLine) = lines
    val times = timesLine.substringAfter(":").trim().split(Regex("""\s+""")).map(String::toLong)
    val distances =
      distanceLine.substringAfter(":").trim().split(Regex("""\s+""")).map(String::toLong)
    times.zip(distances) { t, d -> Race(t, d) }
  }

  val parserVariant = Parser { lines ->
    val (timesLine, distanceLine) = lines
    val time = timesLine.substringAfter(":").trim().replace(" ", "").toLong()
    val distance = distanceLine.substringAfter(":").trim().replace(" ", "").toLong()
    listOf(Race(time, distance))
  }

  fun Race.waysToBeat(): Long {
    // velocity * time = record
    // hold * (duration-hold) = record
    // -hold^2 + hold * duration = record
    // -hold^2 + duration*hold - record = 0
    val a = (-1.0)
    val b = duration.toDouble()
    val c = (-(record + 0.0001)) // have to beat record, not just tie

    // I will never not hear the song in my head...
    val first = ((-b + sqrt(b.pow(2.0) - (4 * a * c))) / (2 * a))
    val second = ((-b - sqrt(b.pow(2.0) - (4 * a * c))) / (2 * a))

    return (floor(second) - ceil(first) + 1).roundToLong()
  }

  override fun example() {
    val CORRECT = 4 * 8 * 9L
    parseInput(filename = "Day6ex.txt")
      .inspect()
      .map { it.waysToBeat() }
      .fold(1, Long::times) shouldBe CORRECT
  }

  override fun part1() {
    val CORRECT = 1710720L
    parseInput().inspect().map { it.waysToBeat() }.fold(1, Long::times) shouldBe CORRECT
  }

  override fun part2() {
    val CORRECT = 35349468L
    parseInput(parser = parserVariant)
      .inspect()
      .map { it.waysToBeat() }
      .fold(1, Long::times) shouldBe CORRECT
  }
}
