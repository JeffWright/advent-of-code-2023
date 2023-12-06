package dev.jtbw.adventofcode.day5

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.toLongs
import dev.jtbw.logsugar.inspect
import dev.jtbw.logsugar.log
import dev.jtbw.scriptutils.ListSplitDelimiterOption.PREPEND
import dev.jtbw.scriptutils.match
import dev.jtbw.scriptutils.shift
import dev.jtbw.scriptutils.shouldBe
import dev.jtbw.scriptutils.size
import dev.jtbw.scriptutils.split

fun main() = Day5.run()

object Day5 : AoCDay<Day5.Input> {

  data class Input(val seeds: List<Long>, val maps: List<Mapping>)

  override val parser = Parser { lines ->
    lines
        .filterNot { it.isBlank() }
        .split(PREPEND) { it.contains("map:") }
        .let {
          val seeds = it.first().single().match(Regex("seeds: (.*)")).split(" ").map(String::toLong)

          val maps =
              it.drop(1) // drop 'seeds:'
                  .map { section ->
                    ConcreteMapping.parse(
                        // First line is "x-to-y map:"
                        description = section.first(),
                        lines = section.drop(1).map { it.toLongs() })
                  }
          Input(seeds, maps)
        }
  }

  override fun part1() {
    val CORRECT = 88_151_870L
    val (seeds, maps) = parseInput()
    solve(seeds.map { it..it }, maps) shouldBe CORRECT
  }

  override fun part2() {
    val CORRECT = 2_008_785L
    val (seeds, maps) = parseInput()
    solve(seeds.windowed(2, 2).map { (start, length) -> start ..< (start + length) }, maps)
        .shouldBe(CORRECT)
  }

  private fun solve(seeds: List<LongRange>, maps: List<Mapping>): Long {
    fun isASeed(long: Long) = seeds.any { long in it }

    val composedMapping = maps.reversed().reduce { b, a -> a.andThen(b) }
    val breakpoints = composedMapping.breakpoints().filter(::isASeed) + seeds.map { it.first }

    log("${seeds.size} seed ranges, ${maps.size} maps")
    log("${breakpoints.size} breakpoints")

    return breakpoints.minOf { seed -> composedMapping(seed) }.inspect("result")
  }

  override fun tests() {
    fun Mapping.bruteForceBreakpoints(): List<Long> {
      val m = this
      return (0..100L).filter { i -> m(i) - 1 != m(i - 1) }.distinct().sorted()
    }

    fun Mapping.checkBreakpoints() {
      // ok to have extra breakpoints (some ranges might be adjacent), error to have missing
      // breakpoints
      val bps = breakpoints()
      val bfbps = bruteForceBreakpoints()
      bfbps.forEach { require(it in bps) }
    }

    parseInput("Day5ex.txt").let { input ->
      val (_, maps) = input
      maps.forEach { it.checkBreakpoints() }

      val t2h = maps[maps.lastIndex - 1]
      val h2l = maps[maps.lastIndex]

      val t2h_then_h2l = t2h.andThen(h2l)

      t2h_then_h2l(0) shouldBe 1L
      t2h_then_h2l(69) shouldBe 0L
      t2h_then_h2l(75) shouldBe 79L

      maps.slice(1..2).let { (s2f, f2w) ->
        val comp = s2f.andThen(f2w)
        (0..100L).forEach { idx -> comp(idx) shouldBe f2w(s2f(idx)) }
        comp.checkBreakpoints() // should include 7
      }

      val composedMapping = maps.reversed().reduce { b, a -> a.andThen(b) }
      composedMapping.checkBreakpoints()
    }
  }
}

interface Mapping {
  val description: String
  fun get(input: Long): Long
  fun reverseGet(output: Long): Long
  fun breakpoints(): List<Long>
}

operator fun Mapping.invoke(input: Long) = get(input)

fun Mapping.toDebugString(out: Long? = null): String {
  return buildString {
    appendLine("[$description")
    if (this@toDebugString is ConcreteMapping) {
      ranges.forEach { r -> appendLine("  " + r) }
      appendLine("  ---")
    }
    breakpoints().forEach { bp -> appendLine("  $bp -> ${get(bp)}") }
    append("]")
  }
}

/** other(this(input)) */
fun Mapping.andThen(b: Mapping): Mapping {
  val a = this
  return object : Mapping {
    override val description: String
      get() = "(${a.description} then ${b.description})"

    override fun get(input: Long): Long {
      return b(a(input))
    }

    override fun reverseGet(output: Long): Long {
      return a.reverseGet(b.reverseGet(output))
    }

    override fun breakpoints(): List<Long> {
      return (a.breakpoints() + b.breakpoints().map { a.reverseGet(it) }).distinct().sorted()
    }
  }
}

class ConcreteMapping(override val description: String, rangez: List<MappedRange>) : Mapping {

  companion object {
    fun parse(description: String, lines: List<List<Long>>) =
        ConcreteMapping(
            description,
            lines.map {
              val (outputStart, inputStart, length) = it
              MappedRange(inputStart ..< (inputStart + length), outputStart)
            })
  }

  val ranges = rangez.sortedBy { it.inputRange.first }

  override fun get(input: Long): Long {
    return ranges.firstNotNullOfOrNull { it(input) } ?: input
  }

  override fun reverseGet(output: Long): Long {
    return ranges.firstNotNullOfOrNull { it.reverseGet(output) } ?: output
  }

  override fun breakpoints(): List<Long> {
    val inputRanges = ranges.map { it.inputRange }
    return inputRanges.map { it.first } + (inputRanges.last().last + 1)
  }
}

data class MappedRange(val inputRange: LongRange, val outputStartingAt: Long) {
  operator fun invoke(input: Long): Long? {
    return if (input in inputRange) {
      outputStartingAt + (input - inputRange.first)
    } else {
      null
    }
  }

  fun reverseGet(output: Long): Long? {
    val outputRange = LongRange(outputStartingAt, outputStartingAt + inputRange.size - 1)
    if (output !in outputRange) {
      return null
    }
    return (output - outputStartingAt + inputRange.first)
  }

  override fun toString(): String {
    return "$inputRange -> ${inputRange.shift(outputStartingAt - inputRange.first)}"
  }
}
