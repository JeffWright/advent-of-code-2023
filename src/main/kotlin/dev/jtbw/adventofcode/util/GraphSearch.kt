package dev.jtbw.adventofcode.util

import dev.jtbw.adventofcode.util.SearchStrategy.BREADTH_FIRST
import dev.jtbw.adventofcode.util.SearchStrategy.DEPTH_FIRST
import java.util.PriorityQueue

enum class SearchStrategy {
  BREADTH_FIRST,
  DEPTH_FIRST
}

interface SearchContext<P> {
  fun search(node: P)

  fun search(node: P, priority: Int)

  fun shortCircuit()
}

data class ComputeStep<P>(
  val node: P,
  val children: List<P>,
)

data class Searchable<T>(val node: T, val priority: Int)

fun <N> traverse(
  strategy: SearchStrategy,
  start: N,
  computeQueue: MutableList<ComputeStep<N>>? = null,
  nodeIdentity: (N) -> Any = { it!! },
  visit: SearchContext<N>.(node: N) -> Unit,
) = traverseMultiStart(strategy, listOf(start), computeQueue, nodeIdentity, visit)

fun <N> traverseMultiStart(
  // TODO JTW if you pass in a strategy and the use search(.., prio), it's misleading
  strategy: SearchStrategy,
  starts: List<N>,
  computeQueue: MutableList<ComputeStep<N>>? = null,
  nodeIdentity: (N) -> Any = { it!! },
  visit: SearchContext<N>.(node: N) -> Unit,
) {
  val compute = computeQueue != null
  val queue = PriorityQueue<Searchable<N>>(compareBy { it.priority })
  val visited: MutableMap<Any, Boolean> = mutableMapOf()
  var prio: Int = 1

  queue.addAll(starts.map { Searchable(it, prio) })

  val hasSearch =
    object : SearchContext<N> {
      var children: MutableList<N>? = null
      var shortCircuited: Boolean = false

      override fun search(node: N) {
        when (strategy) {
          BREADTH_FIRST -> prio++
          DEPTH_FIRST -> prio--
        }
        children?.add(node)
        queue.add(Searchable(node, prio))
      }

      override fun search(node: N, priority: Int) {
        children?.add(node)
        queue.add(Searchable(node, priority))
      }

      override fun shortCircuit() {
        shortCircuited = true
      }
    }

  while (queue.isNotEmpty() && !hasSearch.shortCircuited) {
    val searchNext = queue.poll()
    val node = searchNext.node
    if (visited[nodeIdentity(node)] == true) {
      continue
    }
    visited[nodeIdentity(node)] = true

    if (compute) {
      hasSearch.children = mutableListOf()
    }
    hasSearch.shortCircuited = false
    hasSearch.visit(node)

    if (compute) {
      computeQueue?.add(0, ComputeStep(searchNext.node, hasSearch.children!!))
    }
  }
}

fun <N, R> List<ComputeStep<N>>.compute(operation: (node: N, children: List<R>) -> R): Map<N, R> {
  val computed = mutableMapOf<N, R>()
  this.forEach { (node, children) ->
    computed[node] =
      operation(
        node,
        children.map {
          if (!computed.containsKey(it)) {
            error("NO KEY: $it")
          }
          computed[it]!!
        }
      )
  }
  return computed
}
