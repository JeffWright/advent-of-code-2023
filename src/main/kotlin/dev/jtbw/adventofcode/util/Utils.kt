package dev.jtbw.adventofcode.util

import dev.jtbw.scriptutils.shouldBe

operator fun <T> List<List<T>>.get(x: Int, y: Int) = this[y][x]

operator fun <T> List<List<T>>.get(offset: Offset) = this[offset.y][offset.x]

operator fun <T> List<MutableList<T>>.set(x: Int, y: Int, v: T) {
  this[y][x] = v
}

operator fun <T> List<MutableList<T>>.set(offset: Offset, v: T) {
  this[offset.y][offset.x] = v
}

fun <T> List<List<T>>.inBounds(x: Int, y: Int): Boolean {
  return y in indices && x in this[y].indices
}

fun <T> List<List<T>>.inBounds(offset: Offset): Boolean = inBounds(offset.x, offset.y)

fun <T> List<T>.inBounds(i: Int): Boolean = i in indices

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

fun <T> List<T>.countOf(item: T): Int {
  return count { it == item }
}

typealias Grid<T> = List<List<T>>

typealias MutableGrid<T> = List<MutableList<T>>

fun main() {
  "one, two  ,  three,    four    ,five".splitCommas() shouldBe
      listOf("one", "two", "three", "four", "five")
}
