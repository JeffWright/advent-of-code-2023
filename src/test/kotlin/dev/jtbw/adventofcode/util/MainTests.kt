package dev.jtbw.adventofcode.util

import dev.jtbw.adventofcode.day1.Day1
import dev.jtbw.adventofcode.day10.Day10
import dev.jtbw.adventofcode.day11.Day11
import dev.jtbw.adventofcode.day12.Day12
import dev.jtbw.adventofcode.day13.Day13
import dev.jtbw.adventofcode.day14.Day14
import dev.jtbw.adventofcode.day15.Day15
import dev.jtbw.adventofcode.day16.Day16
import dev.jtbw.adventofcode.day17.Day17
import dev.jtbw.adventofcode.day2.Day2
import dev.jtbw.adventofcode.day3.Day3
import dev.jtbw.adventofcode.day4.Day4
import dev.jtbw.adventofcode.day5.Day5
import dev.jtbw.adventofcode.day6.Day6
import dev.jtbw.adventofcode.day7.Day7
import dev.jtbw.adventofcode.day8.Day8
import dev.jtbw.adventofcode.day9.Day9
import dev.jtbw.adventofcode.run
import dev.jtbw.logsugar.runTiming
import kotlin.test.Test

class MainTests {

  @Test
  fun main() {
    runTiming("Running all days") {
      listOf(
          Day1,
          Day2,
          Day3,
          Day4,
          Day5,
          Day6,
          Day7,
          Day8,
          Day9,
          Day10,
          Day11,
          Day12,
          Day13,
          Day14,
          Day15,
          Day16,
          Day17,
          // Day18,

        )
        .forEach { it.run() }
    }
  }
}
