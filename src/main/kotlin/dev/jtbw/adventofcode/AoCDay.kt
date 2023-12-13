package dev.jtbw.adventofcode

import dev.jtbw.logsugar.ANSI_BRIGHT_GREEN
import dev.jtbw.logsugar.ANSI_BRIGHT_RED
import dev.jtbw.logsugar.LogSugar
import dev.jtbw.logsugar.colorized
import dev.jtbw.logsugar.inspect
import dev.jtbw.logsugar.log
import dev.jtbw.logsugar.logDivider
import dev.jtbw.logsugar.runTiming
import dev.jtbw.scriptutils.PWD
import dev.jtbw.scriptutils.div
import dev.jtbw.scriptutils.invoke
import dev.jtbw.scriptutils.match
import dev.jtbw.scriptutils.stdout
import dev.jtbw.scriptutils.times
import dev.jtbw.scriptutils.waitForOk
import java.io.File
import kotlin.system.exitProcess

interface AoCDay<INPUT> {
  val parser: Parser<INPUT>

  fun example() {}
  fun part1() {}
  fun part2() {}
  fun tests() {}
}

private val inputsDirectory = (PWD / "src/main/resources")

fun <INPUT> AoCDay<INPUT>.parseInput(
  filename: String? = null,
  parser: Parser<INPUT> = this.parser
): INPUT {
  val file =
    if (filename != null) {
      inputsDirectory / filename
    } else {
      val name = (this::class.java.simpleName + ".txt")
      val f = inputsDirectory / name
      if (!f.exists()) {
        download(f)
      }
      f
    }

  return parser.fn(file.readLines())
}

fun download(file: File) {
  val day = file.name.match(Regex("Day(\\d+).txt")).inspect("day")
  val aocSessionToken = (inputsDirectory / "session_token.txt").readText().trim()
  log("Downloading input for day $day...")
  "curl -H \"Cookie: session=$aocSessionToken\" https://adventofcode.com/2023/day/$day/input -o ${file.absolutePath}"()
    .waitForOk(true)
}

fun submitAnswer(day: Int, part: Int, answer: Number) {
  val aocSessionToken = (inputsDirectory / "session_token.txt").readText().trim()
  log("Submitting answer for Day $day Part $part: $answer...")
  val success =
    "curl -H \"Cookie: session=$aocSessionToken\" -H \"Content-Type: application/x-www-form-urlencoded\" https://adventofcode.com/2023/day/$day/answer -d 'level=$part&answer=$answer' -i"()
      .waitForOk(true)
      .stdout
      .contains("That's the right answer!")

  val w = 60
  val color = if (success) ANSI_BRIGHT_GREEN else ANSI_BRIGHT_RED
  val text = if (success) "CORRECT ANSWER" else "WRONG ANSWER"

  log(("█" * w).colorized(color))
  log(("█" * w).colorized(color))
  log((("█" * 20 + "   " + text + "   ").let { it + ("█" * (w - it.length)) }).colorized(color))
  log(("█" * w).colorized(color))
  log(("█" * w).colorized(color))

  if (!success) {
    exitProcess(1)
  }
}

class Parser<T>(val fn: (List<String>) -> T)

fun <INPUT> AoCDay<INPUT>.run() {
  LogSugar.configure(useColors = true)

  val day = this
  logDivider("AoC: ${day.javaClass.simpleName}")
  logDivider("Tests", weight = 5)
  day.tests()
  log("Tests Pass!")

  logDivider("example()", weight = 5)
  day.example()

  logDivider("part1()", weight = 5)
  runTiming("part 1") { day.part1() }

  logDivider("part2()", weight = 5)
  runTiming("part 2") { day.part2() }
}
