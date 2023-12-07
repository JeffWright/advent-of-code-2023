package dev.jtbw.adventofcode

import dev.jtbw.adventofcode.day1.Day1
import dev.jtbw.adventofcode.day2.Day2
import dev.jtbw.adventofcode.day3.Day3
import dev.jtbw.adventofcode.day4.Day4
import dev.jtbw.adventofcode.day5.Day5
import dev.jtbw.adventofcode.day6.Day6
import dev.jtbw.adventofcode.day7.Day7

fun main() {
  listOf(
          Day1,
          Day2,
          Day3,
          Day4,
          Day5,
          Day6,
          Day7,
      )
      .forEach { it.run() }
}
