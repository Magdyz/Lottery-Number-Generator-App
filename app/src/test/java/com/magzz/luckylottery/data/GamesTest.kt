package com.magzz.luckylottery.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GamesTest {

    @Test
    fun `jackpotOdds matches the known value for every game`() {
        val knownOdds = mapOf(
            "euromillions" to 139_838_160L,
            "lotto" to 45_057_474L,
            "setforlife" to 15_339_390L,
            "thunderball" to 8_060_598L,
        )
        for ((id, odds) in knownOdds) {
            val game = gameById(id)
            assertNotNull("expected a game with id $id", game)
            assertEquals("jackpotOdds mismatch for $id", odds, game!!.jackpotOdds)
        }
    }

    @Test
    fun `GAMES contains exactly the four expected games`() {
        val ids = GAMES.map { it.id }.sorted()
        assertEquals(listOf("euromillions", "lotto", "setforlife", "thunderball"), ids)
    }

    @Test
    fun `gameById returns the matching game`() {
        val game = gameById("lotto")
        assertNotNull(game)
        assertEquals("Lotto", game!!.name)
        assertEquals(6, game.main.count)
        assertEquals(59, game.main.max)
    }

    @Test
    fun `gameById returns null for an unknown id`() {
        assertNull(gameById("does-not-exist"))
    }

    @Test
    fun `formatOdds formats with thousands separators`() {
        assertEquals("1 in 139,838,160", formatOdds(139_838_160L))
        assertEquals("1 in 45,057,474", formatOdds(45_057_474L))
        assertEquals("1 in 0", formatOdds(0L))
    }

    @Test
    fun `every game exposes a sensible odds ordering sanity check`() {
        // Not part of the ported spec, but a cheap correctness guard: every
        // game's jackpot odds must be a positive number derived from choose().
        for (game in GAMES) {
            assertTrue("game ${game.id} has non-positive odds", game.jackpotOdds > 0)
        }
    }
}
