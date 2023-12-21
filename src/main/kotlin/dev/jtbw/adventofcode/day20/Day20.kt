package dev.jtbw.adventofcode.day20

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.day20.Day20.Frequency.HIGH
import dev.jtbw.adventofcode.day20.Day20.Frequency.LOW
import dev.jtbw.adventofcode.day20.Day20.Module.Conjunction
import dev.jtbw.adventofcode.day20.Day20.Module.FlipFlop
import dev.jtbw.adventofcode.day20.Day20.Module.Normal
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.leastCommonMultiple
import dev.jtbw.adventofcode.util.splitCommas
import dev.jtbw.logsugar.inspect
import dev.jtbw.logsugar.log
import dev.jtbw.scriptutils.matchGroups
import dev.jtbw.scriptutils.shouldBe

fun main() = Day20.run()

object Day20 : AoCDay<Map<String, Day20.Module>> {
  override val parser = Parser { lines ->
    lines
      .associate {
        val (module, targets) = it.matchGroups(Regex("""(.+) -> (.*)"""))
        val (type, name) = module.matchGroups(Regex("""([%&]?)(.*)"""))

        name to
          when (type) {
            "%" -> FlipFlop(name = name, targets = targets.splitCommas())
            "&" -> Conjunction(name = name, targets = targets.splitCommas())
            else -> Normal(name = name, targets = targets.splitCommas())
          }
      }
      .also { network ->
        network.values.filterIsInstance<Conjunction>().forEach { conj ->
          val inputs = network.values.filter { conj.name in it.targets }
          conj.setInputs(inputs.map { it.name })
        }
      }
  }

  sealed interface Module {
    val name: String
    val targets: List<String>

    fun outPulses(pulse: Pulse): List<Pulse>

    data class Normal(override val name: String, override val targets: List<String>) : Module {
      override fun outPulses(pulse: Pulse): List<Pulse> {
        return targets.map { Pulse(name, it, pulse.freq) }
      }
    }

    data class FlipFlop(override val name: String, override val targets: List<String>) : Module {
      private var on: Boolean = false

      override fun outPulses(pulse: Pulse): List<Pulse> {
        if (pulse.freq == HIGH) {
          return emptyList()
        }
        val sending = if (on) LOW else HIGH
        on = !on
        return targets.map { Pulse(name, it, sending) }
      }
    }

    data class Conjunction(override val name: String, override val targets: List<String>) : Module {
      private var inputMemory = mutableMapOf<String, Frequency>()

      fun setInputs(inputs: List<String>) {
        inputs.forEach { inputMemory[it] = LOW }
      }

      override fun outPulses(pulse: Pulse): List<Pulse> {
        inputMemory[pulse.from] = pulse.freq
        return if (inputMemory.values.all { it == HIGH }) {
          targets.map { Pulse(name, it, LOW) }
        } else {
          targets.map { Pulse(name, it, HIGH) }
        }
      }
    }
  }

  enum class Frequency {
    LOW,
    HIGH
  }

  data class Pulse(val from: String, val target: String, val freq: Frequency)

  override fun part1() {
    val network = parseInput()

    val queue = ArrayDeque<Pulse>()
    var numLow = 0L
    var numHigh = 0L
    repeat(1000) {
      // button
      queue.addLast(Pulse("button", "broadcaster", LOW))
      numLow++
      while (queue.isNotEmpty()) {
        val pulse = queue.removeFirst()
        // log(pulse.toDebugString())

        val module = network[pulse.target] ?: continue
        module.outPulses(pulse).forEach { p ->
          if (p.freq == HIGH) {
            numHigh++
          } else {
            numLow++
          }
          queue.addLast(p)
        }
      }
    }

    (numLow * numHigh).inspect().shouldBe(856482136)
  }

  override fun part2() {
    val network = parseInput()

    // Modules that affect rx
    val l1 = network.values.filter { "rx" in it.targets }.map { it.name }
    // Modules that affect modules that affect rx
    val l2 =
      network.values.filter { a -> l1.any { b -> b in a.targets } }.map { it.name }.toMutableList()

    // This is probably not a coincidence...
    require(l1.all { network[it] is Conjunction })
    require(l2.all { network[it] is Conjunction })

    val cycleLengths = mutableListOf<Int>()
    val queue = ArrayDeque<Pulse>()
    var count = 0
    while (l2.isNotEmpty()) {
      // button
      queue.addLast(Pulse("button", "broadcaster", LOW))
      count++
      while (queue.isNotEmpty()) {
        val pulse = queue.removeFirst()
        val module = network[pulse.target] ?: continue
        module.outPulses(pulse).forEach { p ->
          if (p.freq == HIGH) {
            when {
              pulse.target in l2 -> {
                log("l1: ${pulse.target} @ $count")
                l2 -= pulse.target
                cycleLengths += count
              }
            }
          }
          queue.addLast(p)
        }
      }
    }

    cycleLengths.leastCommonMultiple().inspect().shouldBe(224046542165867)
  }
}
