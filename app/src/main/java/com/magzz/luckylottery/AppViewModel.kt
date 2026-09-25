package com.magzz.luckylottery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.magzz.luckylottery.data.Game
import com.magzz.luckylottery.data.HistoryEntry
import com.magzz.luckylottery.data.HistoryStore
import com.magzz.luckylottery.data.Settings
import com.magzz.luckylottery.data.StoredState
import com.magzz.luckylottery.engine.Generator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class UiState(
    val loaded: Boolean = false,
    /** Newest first. */
    val history: List<HistoryEntry> = emptyList(),
    val settings: Settings = Settings(),
)

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val store = HistoryStore(app)
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()
    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            val stored = withContext(Dispatchers.IO) { store.load() }
            _state.value = UiState(loaded = true, history = stored.history, settings = stored.settings)
        }
    }

    /** Generates lines for [game] using the current settings; returns the new entries. */
    fun generate(game: Game): List<HistoryEntry> {
        val current = _state.value
        val lines = Generator.generateLines(
            game = game,
            count = current.settings.lines,
            smart = current.settings.smart,
            fresh = current.settings.fresh,
            history = current.history.filter { it.gameId == game.id }.map { it.line },
        )
        val now = System.currentTimeMillis()
        val entries = lines.map { HistoryEntry(UUID.randomUUID().toString(), game.id, it, now) }
        mutate { it.copy(history = trim(entries + it.history)) }
        return entries
    }

    fun toggleSaved(id: String) = mutate { s ->
        s.copy(history = s.history.map { if (it.id == id) it.copy(saved = !it.saved) else it })
    }

    fun remove(id: String) = mutate { s -> s.copy(history = s.history.filterNot { it.id == id }) }

    /** Clears history but keeps saved lines. */
    fun clearHistory() = mutate { s -> s.copy(history = s.history.filter { it.saved }) }

    fun updateSettings(transform: (Settings) -> Settings) =
        mutate { it.copy(settings = transform(it.settings)) }

    private fun mutate(transform: (UiState) -> UiState) {
        _state.update(transform)
        scheduleSave()
    }

    // Debounced so rapid taps don't hammer storage.
    private fun scheduleSave() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(300)
            val s = _state.value
            if (!s.loaded) return@launch
            withContext(Dispatchers.IO) { store.save(StoredState(s.history, s.settings)) }
        }
    }

    private fun trim(history: List<HistoryEntry>): List<HistoryEntry> {
        var unsaved = 0
        // Saved lines are kept regardless of the limit.
        return history.filter { it.saved || ++unsaved <= HISTORY_LIMIT }
    }

    companion object {
        const val HISTORY_LIMIT = 200
    }
}
