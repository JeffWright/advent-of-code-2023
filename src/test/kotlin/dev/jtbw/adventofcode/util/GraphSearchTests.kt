package dev.jtbw.adventofcode.util

import dev.jtbw.adventofcode.util.SearchStrategy.*
import dev.jtbw.logsugar.inspect
import dev.jtbw.logsugar.log
import dev.jtbw.logsugar.runTiming
import dev.jtbw.scriptutils.shouldBe
import org.junit.Test

class SearchTests {
  /*
  @Test
  fun testGraphSearch() {
    //
    // (1, 0, 0, 6, 0, 9),
    // (2, 4, 1, 8, 0, 9),
    // (3, 5, 6, 9, 0, 9)
    //
    val input: Grid<Int> =
      listOf(listOf(1, 0, 0, 6, 0, 9), listOf(2, 4, 1, 8, 0, 9), listOf(3, 5, 6, 9, 0, 9))

    logDivider(weight = 5)

    val getNodes = { offset: Offset ->
      Direction.orthogonals
        .map { offset + it }
        .filter { input.inBounds(it) }
        .filter { input[it] != 0 }
    }

    logDivider("breadth first")
    graphSearch(
      strategy = BREADTH_FIRST,
      getNextNodes = getNodes,
      starts = listOf(Offset(0, 0)),
      onVisit = { pos, path ->
        log(input[pos])
        CONTINUE
      },
    )

    logDivider("depth first")
    graphSearch(
      strategy = DEPTH_FIRST,
      getNextNodes = getNodes,
      starts = listOf(Offset(0, 0)),
      onVisit = { pos, path ->
        log(input[pos])
        CONTINUE
      },
    )

    logDivider("shortest path to any 6")

    mutableListOf<List<Offset>>().let { result ->
      graphSearch(
        strategy = BREADTH_FIRST,
        getNextNodes = getNodes,
        starts = listOf(Offset(0, 0)),
        onVisit = { pos, path ->
          if (input[pos] == 6) {
            log("Path to 6: ${path.joinToString()}")
            result += path
            SHORT_CIRCUIT
          } else {
            CONTINUE
          }
        },
      )
      result[0].map(input::get).joinToString() shouldBe "1, 2, 3, 5, 6"
      result.size shouldBe 1
    }

    logDivider("shortest paths to each 6")

    mutableListOf<List<Offset>>().let { result ->
      graphSearch(
        strategy = BREADTH_FIRST,
        getNextNodes = getNodes,
        starts = listOf(Offset(0, 0)),
        onVisit = { pos, path ->
          if (input[pos] == 6) {
            log("Path to 6: ${path.joinToString()}")
            result += path
          }
          CONTINUE
        },
      )

      result[0].map(input::get).joinToString() shouldBe "1, 2, 3, 5, 6"
      result[1].map(input::get).joinToString() shouldBe "1, 2, 4, 1, 8, 6"
      result.size shouldBe 2
    }

    logDivider("all shortest paths to 6")
    mutableListOf<List<Offset>>().let { result ->
      var shortest = Int.MAX_VALUE
      graphSearch(
        strategy = BREADTH_FIRST,
        getNextNodes = getNodes,
        starts = listOf(Offset(0, 0)),
        preventRevisits = false,
        onVisit = { pos, path ->
          log(input[pos])
          if (input[pos] == 6) {
            log("Path to 6: ${path.joinToString()}")
            result += path
            if (path.size < shortest) {
              shortest = path.size
            }
          }

          if (path.size >= shortest) {
            ENDPOINT
          } else {
            CONTINUE
          }
        },
      )
    }

    logDivider("sum of all reachable: value * distance")
    var sum = 0
    graphSearch(
      strategy = BREADTH_FIRST,
      getNextNodes = getNodes,
      starts = listOf(Offset(0, 0)),
      onVisit = { pos, path ->
        sum += input[pos] * path.size
        CONTINUE
      },
    )
    sum shouldBe
      listOf(
          1 * 1,
          2 * 2,
          3 * 3,
          4 * 3,
          5 * 4,
          1 * 4,
          6 * 5,
          8 * 5,
          6 * 6,
          9 * 6,
        )
        .sum()
  }

   */

  @Test
  fun breadthFirst() {
    val order = mutableListOf<Int>()
    val result = mutableListOf<ComputeStep<TreeNode>>()
    runTiming {
      traverse(
        strategy = BREADTH_FIRST,
        start = tree,
        computeQueue = result,
      ) { node ->
        order += node.value
        node.left?.let { search(it) }
        node.right?.let { search(it) }
      }
    }

    order shouldBe listOf(1, 2, 3, 4, 5, 6, 7)
    result
      .compute<TreeNode, Int> { node, children -> node.value + (children.maxOrNull() ?: 0) }
      .get(tree)
      .inspect() shouldBe (1 + 3 + 7)
  }

  @Test
  fun depthFirst() {
    val order = mutableListOf<Int>()
    val result = mutableListOf<ComputeStep<TreeNode>>()
    runTiming {
      traverse(
        strategy = DEPTH_FIRST,
        start = tree,
        computeQueue = result,
      ) { node ->
        order += node.value
        node.left?.let { search(it) }
        node.right?.let { search(it) }
      }
    }

    order shouldBe listOf(1, 3, 7, 6, 2, 5, 4)
    result
      .compute<TreeNode, Int> { node, children ->
        log("compute ${node.value} from $children")
        node.value + (children.maxOrNull() ?: 0)
      }[tree]
      .inspect() shouldBe (1 + 3 + 7)
  }

  @Test
  fun priorityFirst() {
    val order = mutableListOf<Int>()
    val result = mutableListOf<ComputeStep<TreeNode>>()
    runTiming {
      traverse(
        strategy = DEPTH_FIRST,
        start = tree,
        computeQueue = result,
      ) { node ->
        order += node.value
        node.left?.let { search(it, priority = it.value) }
        node.right?.let { search(it, priority = it.value) }
      }
    }

    order shouldBe listOf(1, 2, 3, 4, 5, 6, 7)
    result
      .compute<TreeNode, Int> { node, children ->
        log("compute ${node.value} from $children")
        node.value + (children.maxOrNull() ?: 0)
      }[tree]
      .inspect() shouldBe (1 + 3 + 7)
  }

  private val tree =
    TreeNode(
      1,
      left =
        TreeNode(
          2,
          left = TreeNode(4),
          right = TreeNode(5),
        ),
      right = TreeNode(3, left = TreeNode(6), right = TreeNode(7))
    )

  data class TreeNode(
    val value: Int,
    val left: TreeNode? = null,
    val right: TreeNode? = null,
  ) {
    override fun toString(): String = value.toString()
  }
}
