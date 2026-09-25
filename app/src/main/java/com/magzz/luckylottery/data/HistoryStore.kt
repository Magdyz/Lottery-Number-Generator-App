package com.magzz.luckylottery.data

import android.content.Context
import android.util.AtomicFile
import com.magzz.luckylottery.engine.Line
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class HistoryEntry(
    val id: String,
    val gameId: String,
    val line: Line,
    val createdAt: Long,
    val saved: Boolean = false,
)

data class Settings(
    val smart: Boolean = true, // avoid commonly played patterns
    val fresh: Boolean = true, // avoid repeating your recent numbers
    val lines: Int = 1, // lines per tap: 1, 3 or 5
    val haptics: Boolean = true,
)

data class StoredState(val history: List<HistoryEntry>, val settings: Settings)

/**
 * Persists history and settings as a small JSON file in app-private storage.
 * Nothing ever leaves the device. Writes are atomic so a crash can't corrupt it.
 */
class HistoryStore(context: Context) {
    private val file = AtomicFile(File(context.filesDir, "history.json"))

    fun load(): StoredState = try {
        val root = JSONObject(String(file.readFully(), Charsets.UTF_8))
        StoredState(
            history = root.optJSONArray("history")?.let(::parseHistory) ?: emptyList(),
            settings = root.optJSONObject("settings")?.let(::parseSettings) ?: Settings(),
        )
    } catch (e: Exception) {
        // Missing on first launch, or unreadable: start fresh rather than crash.
        StoredState(emptyList(), Settings())
    }

    fun save(state: StoredState) {
        val root = JSONObject()
            .put("version", 1)
            .put("settings", JSONObject()
                .put("smart", state.settings.smart)
                .put("fresh", state.settings.fresh)
                .put("lines", state.settings.lines)
                .put("haptics", state.settings.haptics))
            .put("history", JSONArray().apply {
                state.history.forEach { e ->
                    put(JSONObject()
                        .put("id", e.id)
                        .put("gameId", e.gameId)
                        .put("main", JSONArray(e.line.main))
                        .put("bonus", JSONArray(e.line.bonus))
                        .put("createdAt", e.createdAt)
                        .put("saved", e.saved))
                }
            })
        val out = file.startWrite()
        try {
            out.write(root.toString().toByteArray(Charsets.UTF_8))
            file.finishWrite(out)
        } catch (e: Exception) {
            file.failWrite(out)
        }
    }

    private fun parseSettings(o: JSONObject) = Settings(
        smart = o.optBoolean("smart", true),
        fresh = o.optBoolean("fresh", true),
        lines = o.optInt("lines", 1).takeIf { it in listOf(1, 3, 5) } ?: 1,
        haptics = o.optBoolean("haptics", true),
    )

    private fun parseHistory(arr: JSONArray): List<HistoryEntry> =
        (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            val gameId = o.optString("gameId")
            if (gameById(gameId) == null) return@mapNotNull null
            HistoryEntry(
                id = o.optString("id"),
                gameId = gameId,
                line = Line(o.getJSONArray("main").toInts(), o.getJSONArray("bonus").toInts()),
                createdAt = o.optLong("createdAt"),
                saved = o.optBoolean("saved"),
            )
        }

    private fun JSONArray.toInts() = (0 until length()).map { getInt(it) }
}
