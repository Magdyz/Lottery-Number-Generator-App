package com.magzz.luckylottery.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magzz.luckylottery.R
import com.magzz.luckylottery.UiState
import com.magzz.luckylottery.data.GAMES
import com.magzz.luckylottery.data.Game
import com.magzz.luckylottery.data.HistoryEntry
import com.magzz.luckylottery.data.Settings
import com.magzz.luckylottery.data.formatOdds
import com.magzz.luckylottery.ui.components.Ball
import com.magzz.luckylottery.ui.components.GlassCard
import com.magzz.luckylottery.ui.components.formatLine
import com.magzz.luckylottery.ui.components.rememberHaptics
import com.magzz.luckylottery.ui.components.shareText
import com.magzz.luckylottery.ui.support.SupportButton
import com.magzz.luckylottery.ui.support.SupportSheet
import com.magzz.luckylottery.ui.theme.LottoColors
import com.magzz.luckylottery.ui.theme.LottoType
import com.magzz.luckylottery.ui.theme.brush
import com.magzz.luckylottery.ui.theme.primary

private const val BALL_STAGGER_MS = 70L
private val LINE_OPTIONS = listOf(1, 3, 5)

@Composable
fun GenerateScreen(
    state: UiState,
    onGenerate: (Game) -> List<HistoryEntry>,
    onToggleSaved: (String) -> Unit,
    onSettingsChange: ((Settings) -> Settings) -> Unit,
    onOpenAbout: () -> Unit,
    contentPadding: PaddingValues,
) {
    val haptics = rememberHaptics(state.settings.haptics)
    val change: ((Settings) -> Settings) -> Unit = {
        haptics.tick()
        onSettingsChange(it)
    }

    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        item { Hero() }
        item { Options(state.settings, change, onOpenAbout) }
        items(GAMES, key = { it.id }) { game ->
            GameCard(
                game = game,
                history = state.history,
                settings = state.settings,
                onGenerate = {
                    haptics.strong()
                    onGenerate(game)
                },
                onToggleSaved = {
                    haptics.tick()
                    onToggleSaved(it)
                },
            )
        }
        item {
            Text(
                "Every combination has the same chance of winning. 18+ · Play responsibly.",
                style = LottoType.caption.copy(color = LottoColors.TextFaint),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun Hero() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(52.dp),
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text("Lucky Lottery", style = LottoType.title)
            Text("Truly random picks, made on your phone.", style = LottoType.caption)
        }
        var showSupport by rememberSaveable { mutableStateOf(false) }
        SupportButton(onClick = { showSupport = true })
        if (showSupport) SupportSheet(onDismiss = { showSupport = false })
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Options(
    settings: Settings,
    onChange: ((Settings) -> Settings) -> Unit,
    onOpenAbout: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val pill = RoundedCornerShape(50)
        Row(
            Modifier
                .fillMaxWidth()
                .clip(pill)
                .background(LottoColors.Surface)
                .border(1.dp, LottoColors.Border, pill)
                .padding(3.dp),
        ) {
            LINE_OPTIONS.forEach { n ->
                val active = settings.lines == n
                val label = if (n == 1) "1 line" else "$n lines"
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(pill)
                        .background(if (active) LottoColors.SurfaceStrong else Color.Transparent)
                        .clickable { onChange { it.copy(lines = n) } }
                        .semantics {
                            role = Role.RadioButton
                            selected = active
                            contentDescription = "$label per tap"
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
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            ToggleChip(Icons.Outlined.Lightbulb, "Smart", settings.smart) {
                onChange { it.copy(smart = !it.smart) }
            }
            ToggleChip(Icons.Outlined.Refresh, "Fresh", settings.fresh) {
                onChange { it.copy(fresh = !it.fresh) }
            }
            IconButton(onClick = onOpenAbout, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = "How Smart and Fresh picks work",
                    tint = LottoColors.TextMuted,
                )
            }
        }
    }
}

@Composable
private fun ToggleChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, active: Boolean, onClick: () -> Unit) {
    val pill = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(pill)
            .background(if (active) LottoColors.Gold else LottoColors.Surface)
            .border(1.dp, if (active) LottoColors.Gold else LottoColors.Border, pill)
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Switch
                stateDescription = if (active) "On" else "Off"
            }
            .padding(horizontal = 14.dp, vertical = 7.dp),
    ) {
        val tint = if (active) LottoColors.OnGold else LottoColors.TextMuted
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = tint, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GameCard(
    game: Game,
    history: List<HistoryEntry>,
    settings: Settings,
    onGenerate: () -> List<HistoryEntry>,
    onToggleSaved: (String) -> Unit,
) {
    val context = LocalContext.current
    // Ids of this session's batch; before the first tap we show the last line played.
    var batchIds by rememberSaveable(game.id) { mutableStateOf<List<String>?>(null) }
    var generation by rememberSaveable(game.id) { mutableIntStateOf(0) }

    val gameHistory = remember(history, game.id) { history.filter { it.gameId == game.id } }
    val lines = remember(batchIds, gameHistory) {
        batchIds?.let { ids ->
            val byId = gameHistory.associateBy { it.id }
            ids.mapNotNull { byId[it] }
        } ?: gameHistory.take(1)
    }
    val isFresh = batchIds != null
    // Fit a whole line on one row: screen minus gutters, card padding and the star button,
    // divided by the ball count (each ball has 3.dp padding per side).
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val perLineBalls = game.main.count + (game.bonus?.count ?: 0)
    // 16.dp spare so dp-to-px rounding can never push the last ball onto a new row.
    val fitted = (screenWidth - 32.dp - 32.dp - 36.dp - 16.dp) / perLineBalls - 6.dp
    val ballSize = minOf(if (lines.size > 1) 36.dp else 44.dp, fitted).coerceAtLeast(24.dp)
    val stagger = if (lines.size > 1) BALL_STAGGER_MS / 2 else BALL_STAGGER_MS

    GlassCard(
        modifier = Modifier
            .background(
                Brush.linearGradient(listOf(game.primary.copy(alpha = 0.2f), Color.Transparent)),
                RoundedCornerShape(24.dp),
            )
            .animateContentSize(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).clip(CircleShape).background(game.primary))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(game.name, style = LottoType.heading)
                Text("${game.format} · ${game.drawDays}", style = LottoType.caption)
            }
            if (lines.isNotEmpty()) {
                IconButton(onClick = {
                    val body = lines.joinToString("\n") { formatLine(game, it) }
                    shareText(context, "${game.name}\n$body\n\nGenerated with Lucky Lottery")
                }) {
                    Icon(
                        Icons.Outlined.Share,
                        contentDescription = "Share ${game.name} numbers",
                        tint = LottoColors.TextMuted,
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .heightIn(min = 52.dp),
        ) {
            if (lines.isEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.Center) {
                    repeat(game.main.count + (game.bonus?.count ?: 0)) {
                        Box(
                            Modifier
                                .padding(3.dp)
                                .size(ballSize.coerceAtMost(40.dp))
                                .clip(CircleShape)
                                .border(1.5.dp, LottoColors.Border, CircleShape),
                        )
                    }
                }
            } else {
                lines.forEachIndexed { li, entry ->
                    val perLine = entry.line.main.size + entry.line.bonus.size
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.semantics(mergeDescendants = true) {
                            contentDescription = buildString {
                                append("${game.name} line: ${entry.line.main.joinToString(", ")}")
                                if (entry.line.bonus.isNotEmpty()) {
                                    append(", ${game.bonus?.label} ${entry.line.bonus.joinToString(", ")}")
                                }
                            }
                        },
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f, fill = false),
                        ) {
                            val key = if (isFresh) "$generation-${entry.id}" else null
                            entry.line.main.forEachIndexed { i, n ->
                                Ball(n, game, size = ballSize, animateKey = key, delayMs = (li * perLine + i) * stagger)
                            }
                            entry.line.bonus.forEachIndexed { i, n ->
                                Ball(
                                    n, game, bonus = true, size = ballSize, animateKey = key,
                                    delayMs = (li * perLine + entry.line.main.size + i) * stagger,
                                )
                            }
                        }
                        IconButton(onClick = { onToggleSaved(entry.id) }, modifier = Modifier.size(36.dp)) {
                            Icon(
                                if (entry.saved) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = if (entry.saved) "Remove from saved" else "Save this line",
                                tint = if (entry.saved) LottoColors.Gold else LottoColors.TextFaint,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
                if (!isFresh) {
                    Text(
                        "Your last line",
                        style = LottoType.caption.copy(color = LottoColors.TextFaint),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Jackpot odds ${formatOdds(game.jackpotOdds)}",
                style = LottoType.caption,
                modifier = Modifier.weight(1f),
            )
            GenerateButton(
                game = game,
                label = if (settings.lines > 1) "${settings.lines} lines" else "Generate",
                onClick = {
                    val entries = onGenerate()
                    batchIds = entries.map { it.id }
                    generation++
                },
            )
        }
    }
}

@Composable
private fun GenerateButton(game: Game, label: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "press")
    val pill = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(pill)
            .background(game.brush)
            .clickable(
                interactionSource = interaction,
                indication = androidx.compose.material3.ripple(),
                role = Role.Button,
                onClickLabel = "Generate ${game.name} numbers",
                onClick = onClick,
            )
            .padding(horizontal = 20.dp)
            .height(44.dp),
    ) {
        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
    }
}
