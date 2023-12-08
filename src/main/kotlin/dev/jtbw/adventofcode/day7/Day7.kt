package dev.jtbw.adventofcode.day7

import dev.jtbw.adventofcode.AoCDay
import dev.jtbw.adventofcode.Parser
import dev.jtbw.adventofcode.parseInput
import dev.jtbw.adventofcode.run
import dev.jtbw.adventofcode.util.splitWhitespace
import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.shouldBe
import dev.jtbw.scriptutils.times

fun main() = Day7.run()

object Day7 : AoCDay<List<Day7.Hand>> {

  override val parser = Parser { lines ->
    lines.map {
      it.splitWhitespace().let { (hand, bid) ->
        Hand(cards = hand.map(Card::fromChar), bid = bid.toInt())
      }
    }
  }

  data class Hand(val cards: List<Card>, val bid: Int, val useJokers: Boolean = false) {

    val type: HandType by lazy {
      if (useJokers) typeWithJokers(cards) else typeWithoutJokers(cards)
    }

    private fun typeWithoutJokers(cards: List<Card>): HandType {
      val cardsMap = cards.groupBy { it }
      return when {
        cardsMap.values.any { it.size == 5 } -> HandType.FiveOfAKind
        cardsMap.values.any { it.size == 4 } -> HandType.FourOfAKind
        cardsMap.values.any { it.size == 3 } && cardsMap.values.any { it.size == 2 } ->
            HandType.FullHouse
        cardsMap.values.any { it.size == 3 } -> HandType.ThreeOfAKind
        cardsMap.values.count { it.size == 2 } == 2 -> HandType.TwoPair
        cardsMap.values.any { it.size == 2 } -> HandType.OnePair
        else -> HandType.HighCard
      }
    }

    private fun typeWithJokers(cards: List<Card>): HandType {
      val cardsNoJokers = cards.filterNot { it == Card.J }
      val cardsMapNoJokers = cardsNoJokers.groupBy { it }
      val numJokers = cards.count { it == Card.J }
      val mostCommonNonJoker = cardsMapNoJokers.maxByOrNull { it.value.size }?.key ?: Card.J
      return typeWithoutJokers(cardsNoJokers + (listOf(mostCommonNonJoker) * numJokers))
    }
  }

  enum class Card {
    _1,
    _2,
    _3,
    _4,
    _5,
    _6,
    _7,
    _8,
    _9,
    T,
    J,
    Q,
    K,
    A;

    companion object {
      fun fromChar(char: Char): Card {
        return when (char) {
          'T',
          'J',
          'Q',
          'K',
          'A' -> valueOf(char.toString())
          else -> valueOf("_$char")
        }
      }
    }
  }

  enum class HandType {
    HighCard,
    OnePair,
    TwoPair,
    ThreeOfAKind,
    FullHouse,
    FourOfAKind,
    FiveOfAKind,
  }

  private val typeComparator = compareBy<Hand> { it.type }

  class TieBreakComparator(val jokers: Boolean) : Comparator<Hand> {
    private val cardComparator = compareBy<Card> { if (jokers && it == Card.J) -1 else it.ordinal }

    override fun compare(a: Hand, b: Hand): Int {
      (0 ..< 5).forEach {
        val comp = cardComparator.compare(a.cards[it], b.cards[it])
        if (comp != 0) {
          return comp
        }
      }
      return 0
    }
  }

  override fun part1() {
    val CORRECT = 247815719
    parseInput()
        .sortedWith(typeComparator.then(TieBreakComparator(jokers = false)))
        .foldIndexed(0) { index, acc, hand -> acc + (hand.bid * (index + 1)) }
        .inspect("result")
        .shouldBe(CORRECT)
  }

  override fun part2() {
    val CORRECT = 248747492
    parseInput()
        .map { it.copy(useJokers = true) }
        .sortedWith(typeComparator.then(TieBreakComparator(jokers = true)))
        .foldIndexed(0) { index, acc, hand -> acc + (hand.bid * (index + 1)) }
        .inspect("result")
        .shouldBe(CORRECT)
  }
}
