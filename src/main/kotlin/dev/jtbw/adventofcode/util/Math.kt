package dev.jtbw.adventofcode.util

import kotlin.math.pow
import kotlin.math.sqrt

/** Sieve of Eratosthenes */
fun primes(upTo: Int): List<Int> {
  val result = BooleanArray(upTo + 1) { true }
  result[0] = false
  result[1] = false
  val max = sqrt(upTo.toDouble()).toInt()
  (2..max).forEach { i -> ((i * i)..upTo).step(i).forEach { k -> result[k] = false } }
  return result.withIndex().filter { it.value }.map { it.index }
}

fun primeFactorization(x: Int): List<Int> {
  val primes = primes(upTo = x / 2)
  val result = mutableListOf<Int>()
  var curr = x

  while (curr != 1) {
    var old = curr
    for (p in primes) {
      if (curr.rem(p) == 0) {
        curr /= p
        result += p
        break
      }
    }
    if(old == curr) {
      result += curr
      break
    }
  }

  return result
}

fun List<Int>.leastCommonMultiple(): Long {
  val nums = this
  val primeFactors = nums.map { primeFactorization(it) }
  val primeFactorsWithMaxCounts =
    primeFactors.flatten().distinct().map { factor ->
      factor to primeFactors.maxOf { it.countOf(factor) }
    }

  return primeFactorsWithMaxCounts.fold(1L) { product, (factor, maxCount) ->
    product * factor.toDouble().pow(maxCount.toDouble()).toLong()
  }
}
