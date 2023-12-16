package dev.jtbw.adventofcode.util.twodeespace

typealias Grid<T> = List<List<T>>

typealias MutableGrid<T> = List<MutableList<T>>

operator fun <T> Grid<T>.get(x: Int, y: Int) = this[y][x]

operator fun <T> Grid<T>.get(offset: Offset) = this[offset.y][offset.x]

operator fun <T> MutableGrid<T>.set(x: Int, y: Int, v: T) {
  this[y][x] = v
}

operator fun <T> MutableGrid<T>.set(offset: Offset, v: T) {
  this[offset.y][offset.x] = v
}

val Grid<*>.width
  get() = this[0].size
val Grid<*>.height
  get() = this.size

fun <T> Grid<T>.inBounds(x: Int, y: Int): Boolean {
  return y in indices && x in this[y].indices
}

fun <T> Grid<T>.inBounds(offset: Offset): Boolean = inBounds(offset.x, offset.y)

fun <T> List<T>.inBounds(i: Int): Boolean = i in indices

fun <T> List<String>.toGrid(transform: (Char) -> T): Grid<T> {
  return map { row -> row.map(transform) }
}

fun <T> Grid<T>.toMultilineString(transform: (T) -> String = { it.toString() }): String {
  return joinToString("\n") { it.joinToString("", transform = transform) }
}

/** @param byColumn: if true, (0, 0), (0, 1), (0, 2) ... (1, 0) */
fun <T> Grid<T>.forEachWithOffset(byColumn: Boolean = false, action: (Offset, T) -> Unit) {
  if (byColumn) {
    (0 ..< width).forEach { x -> (0 ..< height).forEach { y -> action(Offset(x, y), this[x, y]) } }
  } else {
    (0 ..< height).forEach { y -> (0 ..< width).forEach { x -> action(Offset(x, y), this[x, y]) } }
  }
}

/** @param byColumn if true, (0, 0), (0, 1), (0, 2) ... (1, 0) */
fun <T> Grid<T>.asSequenceWithOffset(byColumn: Boolean = false): Sequence<Pair<Offset, T>> {
  val grid = this
  return sequence {
    if (byColumn) {
      (0 ..< width).forEach { x ->
        (0 ..< height).forEach { y -> yield(Offset(x, y) to grid[x, y]) }
      }
    } else {
      (0 ..< height).forEach { y ->
        (0 ..< width).forEach { x -> yield(Offset(x, y) to grid[x, y]) }
      }
    }
  }
}

fun <T> Grid<T>.toMutableGrid(): MutableGrid<T> {
  return this as MutableGrid<T>
}

fun <T> Grid<T>.copy(): Grid<T> = map { row -> row.toList() }
