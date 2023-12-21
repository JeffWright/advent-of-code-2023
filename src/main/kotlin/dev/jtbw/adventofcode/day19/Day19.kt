package dev.jtbw.adventofcode.day19

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.day19.Day19.Condition.Companion.GreaterThan
import dev.jtbw.adventofcode.day19.Day19.Condition.Companion.Just
import dev.jtbw.adventofcode.day19.Day19.Condition.Companion.LessThan
import dev.jtbw.adventofcode.day19.Day19.Input
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.splitCommas
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.matchGroups
import dev.jtbw.scriptutils.shouldBe
import dev.jtbw.scriptutils.size
import dev.jtbw.scriptutils.split

fun main() = Day19.run()

object Day19 : AoCDay<Input> {
  override val parser = Parser { lines ->
    lines
      .split { it.isBlank() }
      .let { (workflows, parts) ->
        Input(
          workflows = workflows.map { Workflow.fromString(it) },
          parts = parts.map { Part.fromString(it) }
        )
      }
  }

  data class Input(val workflows: List<Workflow>, val parts: List<Part>)

  data class Workflow(
    val name: String,
    val conditions: List<Condition>,
  ) {
    companion object {
      fun fromString(str: String): Workflow {
        val (name, ops, final) = str.matchGroups(Regex("""(\w+)\{(.*),([^,]+)\}"""))
        return Workflow(
          name = name,
          conditions = ops.splitCommas().map { Condition.fromString(it) } + Just(final)
        )
      }
    }
  }

  sealed interface Condition {
    val sendTo: String

    companion object {
      fun fromString(str: String): Condition {
        val (attr, ltgt, value, sendTo) = str.matchGroups(Regex("""(\w)([<>])(\d+):(\w+)"""))

        return when (ltgt) {
          "<" -> LessThan(attr.first(), value.toInt(), sendTo)
          ">" -> GreaterThan(attr.first(), value.toInt(), sendTo)
          else -> error(ltgt)
        }
      }

      data class LessThan(
        val attr: Char,
        val value: Int,
        override val sendTo: String,
      ) : Condition {
        override fun toString(): String {
          return "C($attr<$value -> $sendTo)"
        }
      }

      data class GreaterThan(
        val attr: Char,
        val value: Int,
        override val sendTo: String,
      ) : Condition {
        override fun toString(): String {
          return "C($attr>$value -> $sendTo)"
        }
      }

      data class Just(override val sendTo: String) : Condition {

        override fun toString(): String {
          return "J($sendTo)"
        }
      }
    }
  }

  data class Part(val attrs: Map<Char, Int>) {
    companion object {
      fun fromString(str: String): Part {
        return Part(
          attrs =
            str.trim('{', '}').splitCommas().associate {
              val (attr, value) = it.matchGroups(Regex("""(\w)=(\d+)"""))
              attr[0] to value.toInt()
            }
        )
      }
    }
  }

  override fun part1() {
    val input = parseInput()
    val workflows = input.workflows.associateBy { it.name }

    val accepted = acceptedRanges(workflows)
    val acceptedParts = input.parts.filter { part -> accepted.any { part in it } }

    acceptedParts.sumOf { it.attrs.values.sum() }.inspect().shouldBe(397061)
  }

  override fun part2() {
    val input = parseInput()
    val workflows = input.workflows.associateBy { it.name }

    acceptedRanges(workflows).sumOf { it.numPossibilities() }.inspect().shouldBe(125657431183201)
  }

  data class Range(val ranges: Map<Char, IntRange>) {
    operator fun contains(part: Part): Boolean {
      return part.attrs.all { (attr, value) -> value in ranges[attr]!! }
    }
  }

  private fun acceptedRanges(workflows: Map<String, Workflow>): List<Range> {

    val initial =
      Range(
        mapOf(
          'x' to 1..4000,
          'm' to 1..4000,
          'a' to 1..4000,
          's' to 1..4000,
        )
      )

    val root = initial to workflows["in"]!!.conditions

    val queue = ArrayDeque<Pair<Range, List<Condition>>>()
    queue.add(root)

    val acceptedRanges = mutableListOf<Range>()

    while (queue.isNotEmpty()) {
      val (range, conditions) = queue.removeFirst()

      val cond = conditions.first()
      val remainingConditions = conditions.drop(1)

      when (cond) {
        is Just -> {
          when (cond.sendTo) {
            "A" -> {
              acceptedRanges.add(range)
            }
            "R" -> {}
            else -> {
              queue.add(range to workflows[cond.sendTo]!!.conditions)
            }
          }
        }
        is GreaterThan,
        is LessThan -> {
          val attr = (cond as? GreaterThan)?.attr ?: (cond as LessThan).attr
          val value = (cond as? GreaterThan)?.value ?: ((cond as LessThan).value - 1)
          val attrRange = range.ranges[attr]!!
          val (meets, doesNotMeet) =
            attrRange.splitAfter(value).let { if (cond is GreaterThan) it.reverse else it }

          meets?.let {
            val meetsRange = range.copy(ranges = range.ranges + (attr to meets))
            when (cond.sendTo) {
              "A" -> {
                acceptedRanges.add(meetsRange)
              }
              "R" -> {}
              else -> {
                queue.addLast(meetsRange to workflows[cond.sendTo]!!.conditions)
              }
            }
          }
          doesNotMeet?.let {
            val doesNotMeetRange = range.copy(ranges = range.ranges + (attr to doesNotMeet))
            queue.addLast(doesNotMeetRange to remainingConditions)
          }
        }
      }
    }

    return acceptedRanges
  }

  private fun Range.numPossibilities(): Long {
    return ranges.values.fold(1L) { acc, range -> acc * range.size }
  }
}

/** 0..10.splitAfter(5) -> (0..5, 6..10) */
fun IntRange.splitAfter(x: Int): Pair<IntRange?, IntRange?> {
  return if (x < first) {
    null to this
  } else if (x >= last) {
    this to null
  } else {
    (first..x) to (x + 1..last)
  }
}

val <A, B> Pair<A, B>.reverse
  get() = second to first
