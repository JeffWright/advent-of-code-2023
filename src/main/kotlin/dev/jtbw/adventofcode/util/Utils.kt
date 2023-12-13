package dev.jtbw.adventofcode.util

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
fun Int.ascendingRangeTo(other: Int) = minOf(this, other)..maxOf(this, other)

fun <T> List<T>.countOf(item: T): Int = count { it == item }

private operator fun <E> MutableList<E>.set(indices: IntRange, value: E) {
  indices.forEach { this[it] = value }
}

private operator fun <E> MutableList<E>.set(indices: Iterable<Int>, value: E) {
  indices.forEach { this[it] = value }
}
