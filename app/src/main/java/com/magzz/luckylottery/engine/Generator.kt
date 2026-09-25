package com.magzz.luckylottery.engine

import com.magzz.luckylottery.data.Game
import java.security.SecureRandom

/** Returns a uniformly distributed integer in [0, bound). */
fun interface RandomSource {
    fun nextInt(bound: Int): Int
}

/**
 * The OS cryptographically secure generator. SecureRandom.nextInt(bound)
 * uses rejection sampling, so there is no modulo bias.
 */
object SecureRandomSource : RandomSource {
    private val random = SecureRandom()
    override fun nextInt(bound: Int): Int = random.nextInt(bound)
}

data class Line(val main: List<Int>, val bonus: List<Int>) {
    val key: String get() = main.joinToString("-") + "|" + bonus.joinToString("-")
}

/** Pure number-generation logic; no Android dependencies, fully unit-testable. */
object Generator {
    private const val MAX_ATTEMPTS = 500

    /** With "fresh" on, a new line may share at most this many main numbers with a recent line. */
    const val FRESH_MAX_OVERLAP = 2
    const val FRESH_LOOKBACK = 10

    /** Partial Fisher-Yates: uniform, unique, and no retry loop. */
    fun pickUnique(count: Int, max: Int, random: RandomSource): List<Int> {
        val pool = IntArray(max) { it + 1 }
        for (i in 0 until count) {
            val j = i + random.nextInt(max - i)
            val tmp = pool[i]
            pool[i] = pool[j]
            pool[j] = tmp
        }
        return pool.take(count).sorted()
    }

    fun longestRun(sorted: List<Int>): Int {
        var best = 1
        var run = 1
        for (i in 1 until sorted.size) {
            run = if (sorted[i] == sorted[i - 1] + 1) run + 1 else 1
            best = maxOf(best, run)
        }
        return best
    }

    private fun isArithmetic(sorted: List<Int>): Boolean {
        if (sorted.size < 3) return false
        val step = sorted[1] - sorted[0]
        return (2 until sorted.size).all { sorted[it] - sorted[it - 1] == step }
    }

    /**
     * Patterns lots of players pick. Avoiding them does not change the odds of
     * winning, but reduces the chance of sharing a jackpot with others.
     */
    fun isPopularPattern(main: List<Int>, game: Game): Boolean =
        longestRun(main) >= 3 ||
            isArithmetic(main) ||
            (game.main.max > 31 && main.all { it <= 31 })

    private fun randomLine(game: Game, random: RandomSource) = Line(
        main = pickUnique(game.main.count, game.main.max, random),
        bonus = game.bonus?.let { pickUnique(it.count, it.max, random) } ?: emptyList(),
    )

    /**
     * Generate [count] lines for [game].
     * @param history the player's previous lines for this game, newest first.
     */
    fun generateLines(
        game: Game,
        count: Int,
        smart: Boolean,
        fresh: Boolean,
        history: List<Line>,
        random: RandomSource = SecureRandomSource,
    ): List<Line> {
        val seen = history.mapTo(HashSet()) { it.key }
        val recent = ArrayDeque(history.take(FRESH_LOOKBACK).map { it.main })
        val lines = ArrayList<Line>(count)

        repeat(count) {
            var line: Line? = null
            for (attempt in 0 until MAX_ATTEMPTS) {
                val candidate = randomLine(game, random)
                val relaxed = attempt == MAX_ATTEMPTS - 1
                if (candidate.key in seen) continue
                if (!relaxed && smart && isPopularPattern(candidate.main, game)) continue
                if (!relaxed && fresh &&
                    recent.any { r -> r.count { it in candidate.main } > FRESH_MAX_OVERLAP }
                ) continue
                line = candidate
                break
            }
            // Duplicates are astronomically unlikely; this only guards the loop.
            val chosen = line ?: randomLine(game, random)
            seen += chosen.key
            recent.addFirst(chosen.main)
            lines += chosen
        }
        return lines
    }
}
