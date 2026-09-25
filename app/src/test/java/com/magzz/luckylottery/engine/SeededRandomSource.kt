package com.magzz.luckylottery.engine

import java.util.Random

/**
 * Deterministic [RandomSource] backed by [java.util.Random], used to make the
 * property-based and statistical (chi-square) tests reproducible across runs
 * and machines. Not itself a test class.
 */
class SeededRandomSource(seed: Long) : RandomSource {
    private val random = Random(seed)
    override fun nextInt(bound: Int): Int = random.nextInt(bound)
}
