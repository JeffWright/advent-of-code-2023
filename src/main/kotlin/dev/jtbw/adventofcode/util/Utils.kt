package dev.jtbw.adventofcode.util

import dev.jtbw.scriptutils.shouldBe


fun Iterable<String>.toLongs(): List<Long> = map { it.toLong() }

fun Iterable<String>.toInts(): List<Int> = map { it.toInt() }

fun String.splitWhitespace() = split(Regex("""\s+"""))

fun String.splitCommas() = split(Regex("""\s*,\s*"""))

fun <T> Sequence<T>.repeatForever(): Sequence<T> {
  val src = this
  return sequence {
    while (true) {
      yieldAll(src)
    }
  }
}

/** Same as this..other or other..this, depending on which is larger */
fun Int.ascendingRangeTo(other: Int) =
if(other > this) {
  this..other
} else {
  other..this
}

fun <T> List<T>.countOf(item: T): Int {
  return count { it == item }
}

fun main() {
  "one, two  ,  three,    four    ,five".splitCommas() shouldBe
      listOf("one", "two", "three", "four", "five")
}
