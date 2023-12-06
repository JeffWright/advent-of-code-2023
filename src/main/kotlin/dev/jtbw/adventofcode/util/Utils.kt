package dev.jtbw.adventofcode.util

operator fun <T> List<List<T>>.get(x: Int, y: Int) = this[y][x]

operator fun <T> List<MutableList<T>>.set(x: Int, y: Int, v: T) {
  this[y][x] = v
}

fun <T> List<List<T>>.inBounds(x: Int, y: Int): Boolean {
  return y in indices && x in this[y].indices
}

fun <T> List<T>.inBounds(i: Int): Boolean = i in indices

fun List<String>.toLongs(): List<Long> = map { it.toLong() }

fun String.toLongs(delimiter: String = " "): List<Long> = split(delimiter).map { it.toLong() }
