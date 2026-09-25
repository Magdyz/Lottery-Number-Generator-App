package com.magzz.luckylottery.engine

import com.magzz.luckylottery.data.BallSet
import com.magzz.luckylottery.data.GAMES
import com.magzz.luckylottery.data.Game
import com.magzz.luckylottery.data.gameById
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Kotlin/JUnit4 port of tests/engine.test.mjs (the Node spec for the earlier
 * JS version of this engine). Every meaningful case from that file is
 * represented here, adapted to the Kotlin API:
 *   - Generator is an object.
 *   - generateLines(game, count, smart, fresh, history: List<Line>, random).
 *   - Line(main, bonus) exposes .key instead of a free lineKey() function.
 */
class GeneratorTest {

    // -------------------------------------------------------------------
    // pickUnique
    // -------------------------------------------------------------------

    @Test
    fun `pickUnique returns count unique sorted numbers within range`() {
        val random = SeededRandomSource(1)
        repeat(500) {
            val picked = Generator.pickUnique(5, 50, random)
            assertEquals(5, picked.size)
            assertEquals("numbers must be unique", 5, picked.toSet().size)
            for (n in picked) assertTrue("$n out of range", n in 1..50)
            assertEquals("must be sorted ascending", picked.sorted(), picked)
        }
    }

    @Test
    fun `pickUnique with count equal to max returns the full range`() {
        val random = SeededRandomSource(7)
        val picked = Generator.pickUnique(10, 10, random)
        assertEquals((1..10).toList(), picked)
    }

    @Test
    fun `pickUnique works for count 1`() {
        val random = SeededRandomSource(2)
        repeat(100) {
            val picked = Generator.pickUnique(1, 50, random)
            assertEquals(1, picked.size)
            assertTrue(picked[0] in 1..50)
        }
    }

    @Test
    fun `pickUnique with count 1 and max 1 always returns listOf 1`() {
        val random = SeededRandomSource(3)
        assertEquals(listOf(1), Generator.pickUnique(1, 1, random))
    }

    // -------------------------------------------------------------------
    // longestRun
    // -------------------------------------------------------------------

    @Test
    fun `longestRun with no consecutive numbers is 1`() {
        assertEquals(1, Generator.longestRun(listOf(1, 5, 10, 20, 30)))
    }

    @Test
    fun `longestRun detects a run of 3 consecutive numbers`() {
        assertEquals(3, Generator.longestRun(listOf(1, 2, 3, 10, 20)))
    }

    @Test
    fun `longestRun when the whole line is consecutive`() {
        assertEquals(5, Generator.longestRun(listOf(4, 5, 6, 7, 8)))
    }

    @Test
    fun `longestRun takes the longer of two separate runs`() {
        assertEquals(3, Generator.longestRun(listOf(1, 2, 10, 11, 12, 20)))
    }

    @Test
    fun `longestRun for single-element input is 1`() {
        assertEquals(1, Generator.longestRun(listOf(7)))
    }

    // -------------------------------------------------------------------
    // isPopularPattern
    // -------------------------------------------------------------------

    private val euromillions = gameById("euromillions")!! // max 50
    private val lotto = gameById("lotto")!! // max 59

    // A fabricated small-range game (max <= 31) to test the birthday-pattern
    // guard directly, mirroring the fabricated `{ main: { max: 31 } }` object
    // used in the JS suite (Kotlin's Game requires real field values).
    private val smallGame = Game(
        id = "test-small",
        name = "Test",
        main = BallSet(5, 31),
        bonus = null,
        drawDays = "",
        colors = 0L to 0L,
    )

    @Test
    fun `isPopularPattern flags a run of 3 or more consecutive numbers`() {
        assertTrue(Generator.isPopularPattern(listOf(2, 8, 15, 16, 17), euromillions))
    }

    @Test
    fun `isPopularPattern flags an arithmetic sequence`() {
        assertTrue(Generator.isPopularPattern(listOf(2, 4, 6, 8, 10), euromillions))
        assertTrue(Generator.isPopularPattern(listOf(5, 10, 15, 20, 25), euromillions))
    }

    @Test
    fun `isPopularPattern flags all numbers 31 or under when game max exceeds 31`() {
        assertTrue(Generator.isPopularPattern(listOf(1, 7, 13, 19, 31), lotto))
        assertTrue(lotto.main.max > 31)
    }

    @Test
    fun `isPopularPattern does not flag all under 31 when game max is not over 31`() {
        assertFalse(Generator.isPopularPattern(listOf(1, 5, 12, 20, 28), smallGame))
    }

    @Test
    fun `isPopularPattern does not flag a normal unremarkable line`() {
        assertFalse(Generator.isPopularPattern(listOf(3, 17, 22, 38, 49), euromillions))
    }

    @Test
    fun `isPopularPattern does not flag a run of only 2 consecutive numbers`() {
        assertFalse(Generator.isPopularPattern(listOf(3, 17, 22, 23, 49), euromillions))
    }

    // -------------------------------------------------------------------
    // Line.key
    // -------------------------------------------------------------------

    @Test
    fun `Line key joins main and bonus with the expected separators`() {
        val line = Line(main = listOf(1, 2, 3), bonus = listOf(4, 5))
        assertEquals("1-2-3|4-5", line.key)
    }

    @Test
    fun `Line key handles an empty bonus list`() {
        val line = Line(main = listOf(1, 2, 3), bonus = emptyList())
        assertEquals("1-2-3|", line.key)
    }

    @Test
    fun `two lines with the same numbers produce the same key`() {
        val a = Line(main = listOf(1, 2, 3), bonus = listOf(4))
        val b = Line(main = listOf(1, 2, 3), bonus = listOf(4))
        assertEquals(a.key, b.key)
    }

    // -------------------------------------------------------------------
    // generateLines: shape, uniqueness, history for every game
    // -------------------------------------------------------------------

    private fun assertValidLine(line: Line, game: Game) {
        assertEquals(game.main.count, line.main.size)
        assertEquals("main numbers must be sorted", line.main.sorted(), line.main)
        assertEquals("main must be unique", line.main.size, line.main.toSet().size)
        for (n in line.main) assertTrue("main number $n out of range", n in 1..game.main.max)

        val bonus = game.bonus
        if (bonus != null) {
            assertEquals(bonus.count, line.bonus.size)
            assertEquals("bonus numbers must be sorted", line.bonus.sorted(), line.bonus)
            assertEquals("bonus must be unique", line.bonus.size, line.bonus.toSet().size)
            for (n in line.bonus) assertTrue("bonus number $n out of range", n in 1..bonus.max)
        } else {
            assertEquals(emptyList<Int>(), line.bonus)
        }
    }

    @Test
    fun `generateLines returns the requested count with the right shape for every game`() {
        for (game in GAMES) {
            val count = 5
            val lines = Generator.generateLines(
                game, count, smart = false, fresh = false,
                history = emptyList(), random = SeededRandomSource(11),
            )
            assertEquals("game ${game.id}", count, lines.size)
            for (line in lines) assertValidLine(line, game)
        }
    }

    @Test
    fun `generateLines produces unique lines within a batch for every game`() {
        for (game in GAMES) {
            val lines = Generator.generateLines(
                game, 10, smart = false, fresh = false,
                history = emptyList(), random = SeededRandomSource(12),
            )
            val keys = lines.map { it.key }
            assertEquals("game ${game.id}", keys.size, keys.toSet().size)
        }
    }

    @Test
    fun `generateLines never reproduces a line from history for every game`() {
        for (game in GAMES) {
            val history = Generator.generateLines(
                game, 20, smart = false, fresh = false,
                history = emptyList(), random = SeededRandomSource(13),
            )
            val historyKeys = history.map { it.key }.toSet()

            val lines = Generator.generateLines(
                game, 20, smart = false, fresh = false,
                history = history, random = SeededRandomSource(14),
            )
            for (line in lines) {
                assertFalse(
                    "game ${game.id}: generated line ${line.key} duplicates a history line",
                    historyKeys.contains(line.key),
                )
            }
        }
    }

    // -------------------------------------------------------------------
    // generateLines: smart mode avoids popular patterns
    // -------------------------------------------------------------------

    @Test
    fun `generateLines with smart true never emits a flagged pattern across many batches`() {
        for (game in GAMES) {
            val random = SeededRandomSource(21)
            repeat(50) {
                val lines = Generator.generateLines(
                    game, 5, smart = true, fresh = false,
                    history = emptyList(), random = random,
                )
                for (line in lines) {
                    assertFalse(
                        "game ${game.id} smart line ${line.main} was flagged as popular",
                        Generator.isPopularPattern(line.main, game),
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------------
    // generateLines: fresh mode limits overlap with recent history and
    // with earlier lines already produced in the same batch.
    // -------------------------------------------------------------------

    @Test
    fun `generateLines with fresh true keeps overlap within bound vs history and batch`() {
        for (game in GAMES) {
            val history = Generator.generateLines(
                game, 15, smart = false, fresh = false,
                history = emptyList(), random = SeededRandomSource(31),
            )
            val recentHistory = history.take(Generator.FRESH_LOOKBACK)

            val lines = Generator.generateLines(
                game, 8, smart = false, fresh = true,
                history = history, random = SeededRandomSource(32),
            )

            val seenSoFar = ArrayList<List<Int>>()
            for (line in lines) {
                for (h in recentHistory) {
                    val overlap = line.main.count { it in h.main }
                    assertTrue(
                        "game ${game.id} fresh line ${line.main} overlaps history line ${h.main} by $overlap",
                        overlap <= Generator.FRESH_MAX_OVERLAP,
                    )
                }
                for (prev in seenSoFar) {
                    val overlap = line.main.count { it in prev }
                    assertTrue(
                        "game ${game.id} fresh line ${line.main} overlaps earlier batch line $prev by $overlap",
                        overlap <= Generator.FRESH_MAX_OVERLAP,
                    )
                }
                seenSoFar.add(line.main)
            }
        }
    }

    // -------------------------------------------------------------------
    // generateLines: smart and fresh both off still produces valid lines
    // -------------------------------------------------------------------

    @Test
    fun `generateLines with smart and fresh both off still produces valid lines`() {
        for (game in GAMES) {
            val lines = Generator.generateLines(
                game, 5, smart = false, fresh = false,
                history = emptyList(), random = SeededRandomSource(41),
            )
            assertEquals("game ${game.id}", 5, lines.size)
            for (line in lines) assertValidLine(line, game)
        }
    }

    // -------------------------------------------------------------------
    // generateLines: termination and robustness
    // -------------------------------------------------------------------

    @Test
    fun `generateLines with a degenerate always-zero random does not hang and returns count lines`() {
        for (game in GAMES) {
            val start = System.currentTimeMillis()
            val lines = Generator.generateLines(
                game, 5, smart = false, fresh = false,
                history = emptyList(), random = RandomSource { 0 },
            )
            val elapsed = System.currentTimeMillis() - start
            assertEquals("game ${game.id}: must still return the requested count", 5, lines.size)
            assertTrue("game ${game.id} took too long: ${elapsed}ms", elapsed < 5000)
            for (line in lines) {
                // Shape must still hold even though uniqueness may be relaxed.
                assertEquals(game.main.count, line.main.size)
                game.bonus?.let { assertEquals(it.count, line.bonus.size) }
            }
        }
    }

    @Test
    fun `generateLines with degenerate random and smart plus fresh also does not hang`() {
        for (game in GAMES) {
            val start = System.currentTimeMillis()
            val lines = Generator.generateLines(
                game, 5, smart = true, fresh = true,
                history = emptyList(), random = RandomSource { 0 },
            )
            val elapsed = System.currentTimeMillis() - start
            assertEquals("game ${game.id}", 5, lines.size)
            assertTrue("game ${game.id} took too long: ${elapsed}ms", elapsed < 5000)
        }
    }

    @Test
    fun `generateLines is not broken by a large history`() {
        for (game in GAMES) {
            val bigHistory = Generator.generateLines(
                game, 2000, smart = false, fresh = false,
                history = emptyList(), random = SeededRandomSource(51),
            )

            val start = System.currentTimeMillis()
            val lines = Generator.generateLines(
                game, 5, smart = true, fresh = true,
                history = bigHistory, random = SeededRandomSource(52),
            )
            val elapsed = System.currentTimeMillis() - start
            assertEquals("game ${game.id}", 5, lines.size)
            assertTrue(
                "game ${game.id} took too long with large history: ${elapsed}ms",
                elapsed < 5000,
            )
            for (line in lines) assertValidLine(line, game)
        }
    }

    // -------------------------------------------------------------------
    // generateLines: performance sanity check
    // -------------------------------------------------------------------

    @Test
    fun `generateLines smart and fresh generation of 5 lines completes quickly`() {
        for (game in GAMES) {
            val history = Generator.generateLines(
                game, 10, smart = false, fresh = false,
                history = emptyList(), random = SeededRandomSource(62),
            )

            val start = System.currentTimeMillis()
            val lines = Generator.generateLines(
                game, 5, smart = true, fresh = true,
                history = history, random = SeededRandomSource(61),
            )
            val elapsed = System.currentTimeMillis() - start
            assertEquals("game ${game.id}", 5, lines.size)
            // Generous vs. the 200ms used in the JS suite: this is a sanity
            // check against pathological slowness, not a tight benchmark,
            // and JVM unit tests can share the machine with other builds.
            assertTrue("game ${game.id} took ${elapsed}ms, expected a fast completion", elapsed < 3000)
        }
    }
}
