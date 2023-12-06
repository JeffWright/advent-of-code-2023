package dev.jtbw.adventofcode.day4

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.shouldBe

fun main() = Day4.run()

object Day4 : AoCDay<List<Day4.Card>> {

  data class Card(val id: Int, val winningNumbers: List<Int>, val numbers: List<Int>)

  override val parser = Parser { lines ->
    lines.map {
      Regex("""Card +(\d+):(.*)\|(.*)""").matchEntire(it)!!.let {
        Card(
            id = it.groupValues[1].toInt(),
            winningNumbers =
                it.groupValues[2].split(" ").filterNot { it.isBlank() }.map { it.toInt() },
            numbers = it.groupValues[3].split(" ").filterNot { it.isBlank() }.map { it.toInt() },
        )
      }
    }
  }

  override fun part1() {
    val CORRECT = 18519
    parseInput().map { it.expPoints() }.sum().inspect() shouldBe CORRECT
  }

  override fun part2() {
    val CORRECT = 11787590
    val input = parseInput().associateBy { it.id }.mapValues { 1 to it.value }.toMutableMap()

    val lastId = input.keys.max()

    fun addCard(idx: Int, count: Int) {
      println("add $count copies of $idx")
      val pair = input[idx]!!
      input[idx] = pair.copy(first = pair.first + count)
    }

    (1..lastId).forEach { idx ->
      println(idx)
      val numCards = input[idx]!!.first
      val points = input[idx]!!.second.matches()
      (1..points).forEach { addCard(idx + it, numCards) }
    }

    input.inspect()

    input.values.sumOf { it.first }.inspect() shouldBe CORRECT
  }

  fun Card.expPoints(): Int {
    val matches = numbers.count { it in winningNumbers }
    if (matches == 0) {
      return 0
    }
    return Math.pow(2.0, matches - 1.0).toInt()
  }

  fun Card.matches(): Int {
    return numbers.count { it in winningNumbers }
  }
}
