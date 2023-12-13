package dev.jtbw.adventofcode.util

import dev.jtbw.adventofcode.util.SearchStrategy.BREADTH_FIRST
import dev.jtbw.adventofcode.util.SearchStrategy.DEPTH_FIRST
import dev.jtbw.adventofcode.util.ShouldContinue.CONTINUE
import dev.jtbw.adventofcode.util.ShouldContinue.ENDPOINT
import dev.jtbw.adventofcode.util.ShouldContinue.SHORT_CIRCUIT

enum class SearchStrategy {
  BREADTH_FIRST,
  DEPTH_FIRST
}

enum class ShouldContinue {
  /** Search continues */
  CONTINUE,
  /** Search continues, but not expanding from this node */
  ENDPOINT,
  /** Search ends immediately */
  SHORT_CIRCUIT
}

data class SearchMeta<POSITION : Any>(
  val visited: MutableSet<POSITION> = mutableSetOf(),
)

fun <POSITION : Any> graphSearch(
  strategy: SearchStrategy,
  starts: List<POSITION>,
  getNextNodes: (POSITION) -> List<POSITION>,
  onVisit: SearchMeta<POSITION>.(position: POSITION, path: List<POSITION>) -> ShouldContinue,
  preventRevisits: Boolean = true
) {

  val nodeDeque: ArrayDeque<List<POSITION>> = ArrayDeque()

  val visited: MutableSet<POSITION> = mutableSetOf()

  starts.forEach { nodeDeque.add(listOf(it)) }

  // While there are nodes left to search
  while (nodeDeque.isNotEmpty()) {
    // pop a node
    val pathInclusive = nodeDeque.removeFirst()
    val node = pathInclusive.last()
    if (preventRevisits && node in visited) {
      continue
    }
    visited += node

    // Look for nodes reachable from this one
    val adjacents = getNextNodes(node)
    val meta =
      SearchMeta(
        visited = visited,
      )

    // Visit this node
    when (meta.onVisit(node, pathInclusive)) {
      CONTINUE -> {}
      ENDPOINT -> continue
      SHORT_CIRCUIT -> return
    }

    // push reachable nodes onto the stack/queue
    adjacents.forEach { nextNode ->
      if (nextNode in pathInclusive) {
        return@forEach
      }
      when (strategy) {
        BREADTH_FIRST -> nodeDeque.addLast(pathInclusive + nextNode)
        DEPTH_FIRST -> nodeDeque.addFirst(pathInclusive + nextNode)
      }
    }
  }
}
