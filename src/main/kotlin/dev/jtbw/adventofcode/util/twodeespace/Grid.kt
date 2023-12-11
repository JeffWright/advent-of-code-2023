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
  return map { row ->
    row.map(transform)
  }
}

fun <T> Grid<T>.toMultilineString(transform: (T) -> String = {it.toString()}) : String {
  return joinToString("\n") { it.joinToString("" , transform = transform)}
}

// TODO JTW better name
fun <T> Grid<T>._forEachIndexed(action: (Offset, T) -> Unit) {
  forEachIndexed { y, row ->
    row.forEachIndexed { x, t ->
      action(Offset(x, y), t)
    }
  }
}