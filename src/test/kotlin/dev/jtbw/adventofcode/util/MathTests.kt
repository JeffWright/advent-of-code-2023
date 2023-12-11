package dev.jtbw.adventofcode.util

import dev.jtbw.logsugar.inspect
import dev.jtbw.scriptutils.shouldBe
import kotlin.test.Test

class MathTests {
  @Test
  fun testPrimes() {
    primes(100).inspect("primes")
    primeFactorization(82).inspect("82")
    primeFactorization(33).inspect("33")
    primeFactorization(248).inspect("248")
    listOf(82, 33, 248).leastCommonMultiple().inspect("lcm").shouldBe(335544L)
  }
}
