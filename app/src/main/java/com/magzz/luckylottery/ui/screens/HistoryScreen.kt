// History screen: browse, filter, save, share and clear generated lines.
package com.magzz.luckylottery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magzz.luckylottery.UiState
import com.magzz.luckylottery.data.GAMES
import com.magzz.luckylottery.data.Game
import com.magzz.luckylottery.data.HistoryEntry
import com.magzz.luckylottery.data.gameById
import com.magzz.luckylottery.ui.components.Ball
import com.magzz.luckylottery.ui.components.GlassCard
import com.magzz.luckylottery.ui.components.formatLine
import com.magzz.luckylottery.ui.components.rememberHaptics
import com.magzz.luckylottery.ui.components.shareText
import com.magzz.luckylottery.ui.theme.LottoColors
import com.magzz.luckylottery.ui.theme.LottoType
import com.magzz.luckylottery.ui.theme.primary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private enum class Filter { ALL, SAVED }

@Composable
fun HistoryScreen(
    state: UiState,
    onToggleSaved: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClearHistory: () -> Unit,
    contentPadding: PaddingValues,
) {
    val haptics = rememberHaptics(state.settings.haptics)
    var filter by rememberSaveable { mutableStateOf(Filter.ALL) }
    var gameFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    var showClearConfirm by rememberSaveable { mutableStateOf(false) }

    if (!state.loaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = LottoColors.Gold)
        }
        return
    }

    val filtered = remember(state.history, filter, gameFilter) {
        state.history.filter { entry ->
            (filter == Filter.ALL || entry.saved) && (gameFilter == null || entry.gameId == gameFilter)
        }
    }
    val sections = remember(filtered) { buildSections(filtered) }

    LazyColumn(
        contentPadding = contentPadding,
        modifier = Modifier.fillMaxWidth(),
    ) {
        item(key = "header") {
            Column {
                Text("History", style = LottoType.title, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                Segmented(filter) {
                    haptics.tick()
                    filter = it
                }
                Spacer(Modifier.size(8.dp))
                GameChips(gameFilter) {
                    haptics.tick()
                    gameFilter = it
                }
                Spacer(Modifier.size(8.dp))
            }
        }

        if (sections.isEmpty()) {
            item(key = "empty") {
                if (filter == Filter.SAVED) {
                    EmptyState(Icons.Outlined.StarOutline, "Star a line to keep it here.")
                } else {
                    EmptyState(Icons.Outlined.ConfirmationNumber, "No lines yet. Tap a game on the Generate tab.")
                }
            }
        } else {
            sections.forEach { (label, entries) ->
                item(key = "section-$label") {
                    Text(
                        label,
                        style = LottoType.label,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    )
                }
                items(entries, key = { it.id }) { entry ->
                    HistoryRow(
                        entry = entry,
                        onToggleSaved = {
                            haptics.tick()
                            onToggleSaved(entry.id)
                        },
                        onLongPress = {
                            haptics.strong()
                            pendingDeleteId = entry.id
                        },
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            }
        }

        if (state.history.isNotEmpty()) {
            item(key = "clear") {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(onClick = { showClearConfirm = true }) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = LottoColors.Danger,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Clear history", color = LottoColors.Danger)
                    }
                }
            }
        }
    }

    val deleteTarget = pendingDeleteId
    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Delete this line?") },
            text = { Text("This line will be removed from your history.") },
            confirmButton = {
                TextButton(onClick = {
                    onRemove(deleteTarget)
                    pendingDeleteId = null
                }) { Text("Delete", color = LottoColors.Danger) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text("Cancel") }
            },
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear history?") },
            text = { Text("Starred lines will be kept. Everything else will be removed.") },
            confirmButton = {
                TextButton(onClick = {
                    onClearHistory()
                    showClearConfirm = false
                }) { Text("Clear", color = LottoColors.Danger) }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun Segmented(filter: Filter, onChange: (Filter) -> Unit) {
    val pill = RoundedCornerShape(50)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(pill)
            .background(LottoColors.Surface)
            .border(1.dp, LottoColors.Border, pill)
            .padding(3.dp),
    ) {
        SegmentOption("All", filter == Filter.ALL, "Show all lines") { onChange(Filter.ALL) }
        SegmentOption("Saved", filter == Filter.SAVED, "Show saved lines only") { onChange(Filter.SAVED) }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.SegmentOption(
    label: String,
    active: Boolean,
    description: String,
    onClick: () -> Unit,
) {
    val pill = RoundedCornerShape(50)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f)
            .clip(pill)
            .background(if (active) LottoColors.SurfaceStrong else androidx.compose.ui.graphics.Color.Transparent)
            .clickable { onClick() }
            .semantics {
                role = Role.RadioButton
                selected = active
                contentDescription = description
            }
            .padding(vertical = 8.dp),
    ) {
        Text(
            label,
            color = if (active) LottoColors.Text else LottoColors.TextMuted,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GameChips(gameFilter: String?, onChange: (String?) -> Unit) {
    val pill = RoundedCornerShape(50)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Chip(
            label = "All games",
            active = gameFilter == null,
            activeColor = LottoColors.SurfaceStrong,
            activeBorder = LottoColors.Border,
            contentDescription = "Show all games",
        ) { onChange(null) }
        GAMES.forEach { g ->
            val active = gameFilter == g.id
            Chip(
                label = g.name,
                active = active,
                activeColor = g.primary.copy(alpha = 0.2f),
                activeBorder = g.primary,
                contentDescription = "Filter by ${g.name}",
            ) { onChange(if (active) null else g.id) }
        }
    }
}

@Composable
private fun Chip(
    label: String,
    active: Boolean,
    activeColor: androidx.compose.ui.graphics.Color,
    activeBorder: androidx.compose.ui.graphics.Color,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val pill = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(pill)
            .background(if (active) activeColor else androidx.compose.ui.graphics.Color.Transparent)
            .border(1.dp, if (active) activeBorder else LottoColors.Border, pill)
            .clickable { onClick() }
            .semantics {
                role = Role.Button
                selected = active
                this.contentDescription = contentDescription
            }
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            label,
            style = LottoType.caption.copy(color = if (active) LottoColors.Text else LottoColors.TextMuted),
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun HistoryRow(
    entry: HistoryEntry,
    onToggleSaved: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val game = gameById(entry.gameId) ?: return
    val context = LocalContext.current

    GlassCard(
        modifier = modifier.combinedClickable(
            onClick = {},
            onLongClick = onLongPress,
            onClickLabel = null,
            onLongClickLabel = "Delete this line",
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(game.primary))
            Spacer(Modifier.width(8.dp))
            Text(
                game.name,
                style = LottoType.caption.copy(color = LottoColors.Text, fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
            )
            Text(timeLabel(entry.createdAt), style = LottoType.caption)
        }

        Spacer(Modifier.size(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) { FlowRowBalls(game, entry) }
            IconButton(onClick = onToggleSaved, modifier = Modifier.size(40.dp)) {
                Icon(
                    if (entry.saved) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = if (entry.saved) "Remove from saved" else "Save this line",
                    tint = if (entry.saved) LottoColors.Gold else LottoColors.TextMuted,
                    modifier = Modifier.size(20.dp),
                )
            }
            IconButton(
                onClick = {
                    shareText(context, "${game.name}: ${formatLine(game, entry)} (generated with Lucky Lottery)")
                },
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    Icons.Outlined.Share,
                    contentDescription = "Share this line",
                    tint = LottoColors.TextMuted,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowBalls(game: Game, entry: HistoryEntry) {
    FlowRow(horizontalArrangement = Arrangement.Center) {
        entry.line.main.forEach { n -> Ball(n, game, size = 32.dp, animateKey = null) }
        entry.line.bonus.forEach { n -> Ball(n, game, bonus = true, size = 32.dp, animateKey = null) }
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
    ) {
        Icon(icon, contentDescription = null, tint = LottoColors.TextFaint, modifier = Modifier.size(40.dp))
        Text(text, style = LottoType.body.copy(color = LottoColors.TextMuted), textAlign = TextAlign.Center)
    }
}

private fun buildSections(history: List<HistoryEntry>): List<Pair<String, List<HistoryEntry>>> {
    val groups = LinkedHashMap<String, MutableList<HistoryEntry>>()
    for (entry in history) {
        val label = dayLabel(entry.createdAt)
        groups.getOrPut(label) { mutableListOf() }.add(entry)
    }
    return groups.map { it.key to it.value }
}

private fun startOfDay(millis: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun dayLabel(ts: Long): String {
    val diffDays = ((startOfDay(System.currentTimeMillis()) - startOfDay(ts)) / (24 * 60 * 60 * 1000L))
    return when (diffDays) {
        0L -> "Today"
        1L -> "Yesterday"
        else -> SimpleDateFormat("EEE d MMM", Locale.UK).format(java.util.Date(ts))
    }
}

private fun timeLabel(ts: Long): String = SimpleDateFormat("HH:mm", Locale.UK).format(java.util.Date(ts))
