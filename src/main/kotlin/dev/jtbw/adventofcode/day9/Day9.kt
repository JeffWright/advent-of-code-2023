package dev.jtbw.adventofcode.day9

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.splitWhitespace
import dev.jtbw.adventofcode.util.toInts
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.shouldBe

fun main() = Day9.run()

object Day9 : AoCDay<List<List<Int>>> {
  override val parser = Parser { it.map { it.splitWhitespace().toInts() } }

  override fun part1() {
    parseInput().sumOf { it.next() }.inspect().shouldBe(1992273652)
  }

  override fun part2() {
    parseInput().sumOf { it.prev() }.inspect().shouldBe(1012)
  }

  private fun List<Int>.next(): Int {
    return if (this.all { it == 0 }) {
      0
    } else {
      last() + derivative().next()
    }
  }

  private fun List<Int>.prev(): Int {
    return if (this.all { it == 0 }) {
      0
    } else {
      first() - derivative().prev()
    }
  }

  private fun List<Int>.derivative(): List<Int> {
    return windowed(size = 2, step = 1) { (a, b) -> b - a }
  }
}
