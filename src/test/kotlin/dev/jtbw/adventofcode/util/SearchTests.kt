package dev.jtbw.adventofcode.util

import dev.jtbw.adventofcode.util.SearchStrategy.*
import dev.jtbw.adventofcode.util.ShouldContinue.*
import dev.jtbw.adventofcode.util.twodeespace.Direction
import dev.jtbw.adventofcode.util.twodeespace.Offset
import dev.jtbw.adventofcode.util.twodeespace.plus
import dev.jtbw.logsugar.log
import dev.jtbw.logsugar.logDivider
import dev.jtbw.scriptutils.shouldBe
import org.junit.Test

class SearchTests {
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
}
