package dev.jtbw.adventofcode.util

import dev.jtbw.scriptutils.PWD
import dev.jtbw.scriptutils.div

object InputReader {
  fun read(name: String): List<String> {
    return (PWD / "src/main/resources" / "$name").readLines()
  }
  operator fun invoke(name: String) = read(name)
}
