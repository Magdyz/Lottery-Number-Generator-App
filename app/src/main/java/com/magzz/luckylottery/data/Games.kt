package com.magzz.luckylottery.data

import java.text.NumberFormat
import java.util.Locale

data class BallSet(val count: Int, val max: Int, val label: String = "")

/**
 * A lottery game. Ranges and draw days are for the UK National Lottery.
 * Odds are computed from the ranges so they can never drift out of sync.
 */
data class Game(
    val id: String,
    val name: String,
    val main: BallSet,
    val bonus: BallSet?,
    val drawDays: String,
    /** Gradient pair as ARGB, kept as Longs so this file has no UI dependency. */
    val colors: Pair<Long, Long>,
) {
    val jackpotOdds: Long =
        choose(main.max, main.count) * (bonus?.let { choose(it.max, it.count) } ?: 1L)

    val format: String =
        if (bonus == null) "${main.count} from ${main.max}"
        else "${main.count} from ${main.max} + ${bonus.count} from ${bonus.max}"

    val formatLong: String =
        if (bonus == null) format else "$format ${bonus.label}"
}

fun choose(n: Int, k: Int): Long {
    var result = 1L
    for (i in 1..k) result = result * (n - k + i) / i
    return result
}

fun formatOdds(odds: Long): String =
    "1 in " + NumberFormat.getIntegerInstance(Locale.UK).format(odds)

val GAMES = listOf(
    Game(
        id = "euromillions",
        name = "EuroMillions",
        main = BallSet(5, 50),
        bonus = BallSet(2, 12, "Lucky Stars"),
        drawDays = "Tue & Fri",
        colors = 0xFF4F8CFF to 0xFF2B5BDB,
    ),
    Game(
        id = "lotto",
        name = "Lotto",
        main = BallSet(6, 59),
        bonus = null,
        drawDays = "Wed & Sat",
        colors = 0xFFFF5C7A to 0xFFD6204A,
    ),
    Game(
        id = "setforlife",
        name = "Set For Life",
        main = BallSet(5, 47),
        bonus = BallSet(1, 10, "Life Ball"),
        drawDays = "Mon & Thu",
        colors = 0xFF2FD3B5 to 0xFF0E9F87,
    ),
    Game(
        id = "thunderball",
        name = "Thunderball",
        main = BallSet(5, 39),
        bonus = BallSet(1, 14, "Thunderball"),
        drawDays = "Tue, Wed, Fri & Sat",
        colors = 0xFFA57BFF to 0xFF6D3FE0,
    ),
)

fun gameById(id: String): Game? = GAMES.firstOrNull { it.id == id }
