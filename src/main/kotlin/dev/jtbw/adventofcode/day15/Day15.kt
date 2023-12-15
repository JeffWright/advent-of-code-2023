package dev.jtbw.adventofcode.day15

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.parseInputRaw
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.splitCommas
import dev.jtbw.scriptutils.shouldBe

fun main() = Day15.run()

object Day15 : AoCDay<List<String>> {
  override val parser = Parser { lines -> lines.joinToString("\n").splitCommas() }

  val h = { i: String -> i.fold(0) { h, c -> ((h + c.code) * 17).rem(256) } }

  override fun part1() {
    parseInput().sumOf(h).shouldBe(521341)
  }

  override fun part2() {
    val r = 0..255
    val bs = r.map { mutableMapOf("" to (9999 to 0)) }
    var (l, k) = "" to 0
    parseInputRaw().forEach { c ->
      when (c) {
        in 'a'..'z' -> l += c
        '-' -> bs[h(l)] -= l
        in '0'..'9' -> bs[h(l)].compute(l) { _, e -> (e?.first ?: k++) to c - '0' }
        ',' -> l = ""
      }
    }

    val s = 1..256
    bs
      .zip(s) { box, k -> k * box.values.sortedBy { it.first }.zip(s) { (_, l), i -> i * l }.sum() }
      .sum()
      .shouldBe(252782)
  }
}
