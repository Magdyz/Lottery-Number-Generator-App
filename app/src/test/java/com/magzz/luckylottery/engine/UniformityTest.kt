package com.magzz.luckylottery.engine

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Chi-square uniformity checks for [Generator.pickUnique], seeded for
 * reproducibility. Thresholds are generous relative to the standard critical
 * values to avoid flakiness while still catching a genuinely biased/broken
 * generator:
 *   df=49 (main, 5 of 50)  -> critical ~85.35 at p~=0.001, we assert < 100
 *   df=11 (bonus, 2 of 12) -> critical ~31.26 at p~=0.001, we assert < 45
 */
class UniformityTest {

    private fun chiSquare(counts: List<Int>, expected: Double): Double =
        counts.sumOf { c -> (c - expected) * (c - expected) / expected }

    @Test
    fun `EuroMillions main numbers 5 of 50 are uniformly distributed`() {
        val random = SeededRandomSource(42)
        val draws = 200_000
        val counts = IntArray(51)
        repeat(draws) {
            for (n in Generator.pickUnique(5, 50, random)) counts[n]++
        }
        val expected = draws * 5.0 / 50
        val chi2 = chiSquare(counts.drop(1), expected)
        assertTrue(
            "chi-square ${"%.2f".format(chi2)} exceeds generous threshold (df=49, ~85.35 critical)",
            chi2 < 100,
        )
    }

    @Test
    fun `EuroMillions bonus numbers 2 of 12 are uniformly distributed`() {
        val random = SeededRandomSource(42)
        val draws = 200_000
        val counts = IntArray(13)
        repeat(draws) {
            for (n in Generator.pickUnique(2, 12, random)) counts[n]++
        }
        val expected = draws * 2.0 / 12
        val chi2 = chiSquare(counts.drop(1), expected)
        assertTrue(
            "chi-square ${"%.2f".format(chi2)} exceeds generous threshold (df=11, ~31.26 critical)",
            chi2 < 45,
        )
    }
}
