package com.magzz.luckylottery.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magzz.luckylottery.data.Game
import com.magzz.luckylottery.data.HistoryEntry
import com.magzz.luckylottery.ui.theme.Dimens
import com.magzz.luckylottery.ui.theme.LottoColors
import com.magzz.luckylottery.ui.theme.secondary
import kotlinx.coroutines.delay

/**
 * A single lottery ball. When [animateKey] changes, it springs in after [delayMs].
 * Pass animateKey = null for a static ball (e.g. in history).
 */
@Composable
fun Ball(
    number: Int,
    game: Game,
    modifier: Modifier = Modifier,
    bonus: Boolean = false,
    size: Dp = 44.dp,
    animateKey: Any? = null,
    delayMs: Long = 0,
) {
    val progress = remember(animateKey) { Animatable(if (animateKey == null) 1f else 0f) }
    LaunchedEffect(animateKey) {
        if (animateKey == null) return@LaunchedEffect
        delay(delayMs)
        progress.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow))
    }

    val fill = if (bonus) listOf(LottoColors.Gold, LottoColors.GoldDeep)
    else listOf(Color.White, Color(0xFFE4E8F7))
    val textColor = if (bonus) LottoColors.OnGold else game.secondary

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(3.dp)
            .size(size)
            .graphicsLayer {
                val p = progress.value
                alpha = (p / 0.4f).coerceIn(0f, 1f)
                scaleX = 0.3f + 0.7f * p
                scaleY = 0.3f + 0.7f * p
                rotationZ = -90f * (1f - p)
            }
            .shadow(4.dp, CircleShape)
            .background(Brush.linearGradient(fill), CircleShape),
    ) {
        // Glossy highlight.
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = size * 0.1f)
                .size(width = size * 0.45f, height = size * 0.2f)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.55f)),
        )
        Text(
            text = number.toString(),
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            // Fixed size: balls must not overflow with large system fonts.
            fontSize = (size.value * 0.42f).sp.nonScaled(),
        )
    }
}

/** Converts sp so the rendered size ignores the user's font scale. */
@Composable
private fun androidx.compose.ui.unit.TextUnit.nonScaled() =
    (value / androidx.compose.ui.platform.LocalDensity.current.fontScale).sp

/** Standard translucent card used across screens. */
@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Dimens.cardShape)
            .background(LottoColors.Surface)
            .border(1.dp, LottoColors.Border, Dimens.cardShape)
            .padding(16.dp),
        content = content,
    )
}

/** Haptics that respect the user's setting. */
class Haptics(private val perform: (HapticFeedbackType) -> Unit, private val enabled: Boolean) {
    fun tick() { if (enabled) perform(HapticFeedbackType.TextHandleMove) }
    fun strong() { if (enabled) perform(HapticFeedbackType.LongPress) }
}

@Composable
fun rememberHaptics(enabled: Boolean): Haptics {
    val feedback = LocalHapticFeedback.current
    return remember(feedback, enabled) { Haptics(feedback::performHapticFeedback, enabled) }
}

fun formatLine(game: Game, entry: HistoryEntry): String {
    val main = entry.line.main.joinToString("  ")
    return if (entry.line.bonus.isEmpty()) main else "$main   ★ ${entry.line.bonus.joinToString("  ")}"
}

fun shareText(context: Context, text: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(send, null))
}
