package dev.jtbw.adventofcode.day12

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.day12.Day12.SpringRow
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.splitCommas
import dev.jtbw.adventofcode.util.splitWhitespace
import dev.jtbw.adventofcode.util.toInts
import dev.jtbw.adventofcode.util.twodeespace.inBounds
import dev.jtbw.logsugar.inspect
import dev.jtbw.logsugar.logDivider
import dev.jtbw.scriptutils.shouldBe
import dev.jtbw.scriptutils.times

fun main() = Day12.run()

object Day12 : AoCDay<List<SpringRow>> {

  override val parser = Parser { lines ->
    lines.map {
      it.splitWhitespace().let { (springs, groups) ->
        SpringRow(springs, groups.splitCommas().toInts())
      }
    }
  }

  data class SpringRow(val springs: String, val groups: List<Int>)

  private const val UNKNOWN = '?'
  private const val SPRING = '#'
  private const val EMPTY = '.'
  private val CONCRETES = listOf(SPRING, EMPTY)

  override fun tests() {
    val input = parseInput("Day12ex.txt")

    input[0].numArrangements() shouldBe 1L
    input[1].numArrangements() shouldBe 4L
    input[2].numArrangements() shouldBe 1L
    input[3].numArrangements() shouldBe 1L
    input[4].numArrangements() shouldBe 4L
    logDivider(weight = 5)
    input[5].inspect().numArrangements() shouldBe 10L

    input.sumOf { it.numArrangements() } shouldBe 21L

    val unfolded = parseInput("Day12ex.txt").map { it.unfoldedForPart2() }

    unfolded.first().let {
      it.springs shouldBe "???.###????.###????.###????.###????.###"
      it.groups shouldBe "1,1,3,1,1,3,1,1,3,1,1,3,1,1,3".splitCommas().toInts()
    }

    unfolded[0].numArrangements() shouldBe 1L
    unfolded[1].numArrangements() shouldBe 16384L
    unfolded[2].numArrangements() shouldBe 1L
    unfolded[3].numArrangements() shouldBe 16L
    unfolded[4].numArrangements() shouldBe 2500L
    unfolded[5].inspect().numArrangements() shouldBe 506250L
  }

  override fun part1() {
    parseInput().sumOf { it.numArrangements() }.inspect() shouldBe 6827L
  }

  override fun part2() {
    parseInput().map { it.unfoldedForPart2() }.sumOf { it.numArrangements() }.inspect() shouldBe
      1537505634471L
  }

  private fun SpringRow.unfoldedForPart2(): SpringRow {
    return copy(springs = ((springs + UNKNOWN) * 5).dropLast(1), groups = groups * 5)
  }

  data class State(
    val idx: Int,
    val groupInProgress: Boolean,
    val currentGroup: Int,
    // If null, ran out of groups
    val leftInGroup: Int?
  )

  private fun SpringRow.numArrangements(): Long {
    val startingState =
      State(idx = 0, groupInProgress = false, currentGroup = 0, leftInGroup = groups[0])
    return numArrangementsInner(startingState, mutableMapOf())
  }

  private fun SpringRow.numArrangementsInner(state: State, memo: MutableMap<State, Long>): Long {
    val springs = springs.toList()

    if (!springs.inBounds(state.idx)) {
      return if (((state.leftInGroup ?: 0) == 0) && state.currentGroup >= groups.lastIndex) {
        // Reached end and is valid.  Count it!
        1
      } else {
        0
      }
    }

    val char = springs[state.idx]

    val options =
      if (char == UNKNOWN) {
        CONCRETES
      } else {
        listOf(char)
      }

    return options.sumOf { option ->
      val nextState = state.nextState(option, this)
      when {
        nextState == null -> 0
        memo.containsKey(nextState) -> {
          memo[nextState]!!
        }
        else -> {
          numArrangementsInner(nextState, memo).also { memo[nextState] = it }
        }
      }
    }
  }

  private fun State.nextState(char: Char, row: SpringRow): State? {
    require(char != UNKNOWN)

    when (char) {
      SPRING -> {
        return if (groupInProgress && leftInGroup == 0) {
          // Group is supposed to end but doesn't
          null
        } else if (leftInGroup == null) {
          // There aren't supposed to be any more groups
          null
        } else {
          this.copy(
            idx = idx + 1,
            groupInProgress = true,
            currentGroup = currentGroup,
            leftInGroup = leftInGroup - 1
          )
        }
      }
      EMPTY -> {
        if (groupInProgress && leftInGroup != 0) {
          // Group wasn't supposed to end, but did
          return null
        }
        if (groupInProgress) {
          return this.copy(
            idx = idx + 1,
            groupInProgress = false,
            currentGroup = currentGroup + 1,
            leftInGroup = row.groups.getOrNull(currentGroup + 1)
          )
        } else {
          return this.copy(
            idx = idx + 1,
            groupInProgress = false,
          )
        }
      }
      else -> error("bad char $char")
    }
  }
}
