package dev.jtbw.adventofcode.util

import dev.jtbw.adventofcode.util.twodeespace.Offset

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

/** Same as this..other or other..this, depending on which is larger */
fun Long.ascendingRangeTo(other: Long) = minOf(this, other)..maxOf(this, other)

fun <T> List<T>.countOf(item: T): Int = count { it == item }

private operator fun <E> MutableList<E>.set(indices: IntRange, value: E) {
  indices.forEach { this[it] = value }
}

private operator fun <E> MutableList<E>.set(indices: Iterable<Int>, value: E) {
  indices.forEach { this[it] = value }
}

/* Credit: https://rosettacode.org/wiki/Shoelace_formula_for_polygonal_area */
fun shoelaceArea(v: List<Offset>): Double {
  val n = v.size
  var a = 0.0
  for (i in 0 ..< n - 1) {
    require(v[i].x == v[i + 1].x || v[i].y == v[i + 1].y)
    a += v[i].x * v[i + 1].y - v[i + 1].x * v[i].y
  }
  require(v[0].x == v[n - 1].x || v[0].y == v[n - 1].y)
  return Math.abs(a + v[n - 1].x * v[0].y - v[0].x * v[n - 1].y) / 2.0
}
