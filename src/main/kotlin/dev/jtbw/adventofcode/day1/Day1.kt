package dev.jtbw.adventofcode.day1

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.shouldBe

fun main() {
  Day1.run()
}

object Day1 : AoCDay<List<String>> {

  override val parser = Parser { it }

  override fun part1() {
    val CORRECT = 54390
    val digits = 0..9

    fun String.numAt(idx: Int): Int? {
      val str = this
      digits
        .firstOrNull { digit -> str[idx].digitToIntOrNull() == digit }
        ?.let {
          return it
        }

      return null
    }

    parseInput()
      .map { line ->
        var first: Int? = null
        var last: Int? = null

        for (idx in line.indices) {
          first = line.numAt(idx)
          if (first != null) {
            break
          }
        }

        for (idx in line.indices.reversed()) {
          last = line.numAt(idx)
          if (last != null) {
            break
          }
        }
        (line to (first!! * 10 + last!!))
        // .inspect()
      }
      .sumOf { it.second }
      .inspect() shouldBe CORRECT
  }

  override fun part2() {
    val CORRECT = 54277
    val digits = 0..9
    val names =
      listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun String.numAt(idx: Int): Int? {
      val str = this
      digits
        .firstOrNull { digit -> str[idx].digitToIntOrNull() == digit }
        ?.let {
          return it
        }

      names
        .firstOrNull { name ->
          idx + name.length <= str.length && str.substring(idx, idx + name.length) == name
        }
        ?.let {
          return names.indexOf(it)
        }

      return null
    }

    parseInput()
      .map { line ->
        var first: Int? = null
        var last: Int? = null

        for (idx in line.indices) {
          first = line.numAt(idx)
          if (first != null) {
            break
          }
        }

        for (idx in line.indices.reversed()) {
          last = line.numAt(idx)
          if (last != null) {
            break
          }
        }
        (line to (first!! * 10 + last!!))
        // .inspect()
      }
      .sumOf { it.second }
      .inspect() shouldBe CORRECT
  }
}
