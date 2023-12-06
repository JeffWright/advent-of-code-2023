package dev.jtbw.adventofcode

import dev.jtbw.adventofcode.util.InputReader
import dev.jtbw.logsugar.log
import dev.jtbw.logsugar.logDivider

interface AoCDay<INPUT> {
  val parser: Parser<INPUT>

  fun example() {}
  fun part1() {}
  fun part2() {}
  fun tests() {}
}

fun <INPUT> AoCDay<INPUT>.parseInput(
    filename: String? = null,
    parser: Parser<INPUT> = this.parser
): INPUT {
  val filename = filename ?: (this::class.java.simpleName + ".txt")
  return parser.fn(InputReader.read(filename))
}

class Parser<T>(val fn: (List<String>) -> T)

fun <INPUT> AoCDay<INPUT>.run() {
  val day = this
  logDivider("AoC: ${day.javaClass.simpleName}")
  logDivider("Tests", weight = 5)
  day.tests()
  log("Tests Pass!")

  logDivider("example()", weight = 5)
  day.example()

  logDivider("part1()", weight = 5)
  day.part1()

  logDivider("part2()", weight = 5)
  day.part2()
}
