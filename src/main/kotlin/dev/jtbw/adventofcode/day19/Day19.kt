package dev.jtbw.adventofcode.day19

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.day19.Day19.Input
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.submitAnswer
import dev.jtbw.adventofcode.util.splitCommas
import dev.jtbw.logsugar.inspect
import dev.jtbw.logsugar.log
import dev.jtbw.scriptutils.matchGroups
import dev.jtbw.scriptutils.second
import dev.jtbw.scriptutils.shouldBe
import dev.jtbw.scriptutils.split
import dev.jtbw.scriptutils.third

fun main() = Day19.run()

object Day19 : AoCDay<Input> {
  override val parser = Parser { lines ->
    lines.split { it.isBlank() }.let { (workflows, parts) ->
      Input(
        workflows = workflows.map { Workflow.fromString(it) },
        parts = parts.map { Part.fromString(it) }
      )
    }
  }

  data class Input(
    val workflows: List<Workflow>,
    val parts: List<Part>
  )

  data class Workflow(
    val  name: String,
    val conditions: List<Condition>,
    val final: String

  ) {
    companion object {
      fun fromString(str: String) : Workflow {
        //val (name, ops, final) = str.matchGroups(Regex("""(\w+)\{([^,]+),([^,]+)\}""")) // TODO JTW
        val (name, ops, final) = str.matchGroups(Regex("""(\w+)\{(.*),([^,]+)\}"""))
        name.inspect("name")
        ops.inspect("ops")
        final.inspect("final")
        return Workflow(
          name = name,
          conditions = ops.splitCommas().map { Condition.fromString(it) },
          final = final
        )
      }
    }
  }

  data class Condition(
    val attr: Char,
    val lessThan: Boolean,
    val value: Int,
    val sendTo: String
  ) {
    companion object {
      fun fromString(str: String) : Condition {
        val (attr, lessThan, value, sendTo) = str.matchGroups(
          Regex("""(\w)([<>])(\d+):(\w+)""")
        )

        return Condition(
          attr = attr[0],
          lessThan = lessThan == "<",
          value = value.toInt(),
          sendTo = sendTo
        )
      }
    }
  }

  data class Part (
    val attrs: Map<Char, Int>
  ) {
    companion object {
      fun fromString(str: String) : Part {
        return Part(
          attrs = str.trim('{', '}')
          .splitCommas()
          .map {
            val (attr, value) = it.matchGroups(Regex("""(\w)=(\d+)"""))
            attr[0] to value.toInt()
          }
          .toMap()
        )
      }
    }
  }

  private fun Workflow.get(part: Part) : String {
    return conditions.firstNotNullOfOrNull {
      it.get(part)
    }
      ?: this.final
  }

  private fun Condition.get(part: Part): String? {
     val a = part.attrs[attr]!!
    if((lessThan && a < value) || (!lessThan && a > value)) {
      return sendTo
    } else {
      return null
    }
  }

  fun isAccepted(
    part: Part,
    workflows: Map<String, Workflow>,
    firstWorkflow: String
  ): Boolean {
    part.inspect("part")
    var  w = firstWorkflow
    while(w != "A" && w != "R") {
      w = workflows[w]!!.get(part)
      w.inspect("w")
    }
    return w == "A"
  }

  override fun part1() {
    val input = parseInput().inspect()

    val workflows = input.workflows.associateBy { it.name }

    val firstWorkflow = "in"

    val accepted = input.parts.filter {
      isAccepted(
        it,
        workflows,
        firstWorkflow
      )
        .inspect("isAccepted")
    }

    accepted.sumOf { it.attrs.values.sum() }
      .shouldBe(397061)
  }

  override fun part2() {
    (4000L * 4000L * 4000L * 4000L) shouldBe (2.56e+14).toLong()
    (4000L * 4000L * 4000L * 4000L).inspect()
    Long.MAX_VALUE.inspect()

    //qkq{x<1416:A,crn}

    //A: 0..1416  else: result(crn)

    //crn{x>2662:A,RFG}

    //A: 0..1416, 2662..4000 else: rfg

    // rfg{s<537:gd,x>2440:R,A}

    //A: 0..1416, 2662..4000,  R: 2440..2662,   else: A

    // A: 0..1416, 2662..4000  U:
    listOf(0..<1416, 2662..4000)
    parseInput().workflows
      .filter {w ->
        val grouped = w.conditions.groupBy { cond -> cond.attr }
        grouped.any { it.value.count() > 1 }
      }
      .inspect("count")

  }
}
