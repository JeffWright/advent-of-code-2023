package dev.jtbw.adventofcode.util

import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.shouldBe
import kotlin.test.Test

class MathTests {
  @Test
  fun testPrimes() {
    primes(100).inspect("primes")
    primeFactorization(82).shouldBe(listOf(2, 41))
    primeFactorization(33).shouldBe(listOf(3, 11))
    primeFactorization(248).shouldBe(listOf(2, 2, 2, 31))
    primeFactorization(3739) shouldBe listOf(3739) // prime
    listOf(82, 33, 248).leastCommonMultiple().inspect("lcm").shouldBe(335544L)
  }
}
