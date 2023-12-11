package dev.jtbw.adventofcode

import dev.jtbw.logsugar.inspect

fun main() = Template.run()

object Template : AoCDay<Int> {
  override val parser = Parser { lines ->
    TODO("not yet implemented")
    0
  }

  override fun example() {
    parseInput("DayNex.txt").inspect()
  }
}
