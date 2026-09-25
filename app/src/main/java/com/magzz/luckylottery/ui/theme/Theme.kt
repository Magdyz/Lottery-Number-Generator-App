package com.magzz.luckylottery.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magzz.luckylottery.data.Game

/** Night-sky palette with gold accents, matching the lotus icon. */
object LottoColors {
    val Night = Color(0xFF0B1026)
    val NightMid = Color(0xFF171A3F)
    val NightEnd = Color(0xFF241845)
    val Surface = Color(0x0FFFFFFF) // 6% white
    val SurfaceStrong = Color(0x1AFFFFFF) // 10% white
    val Border = Color(0x1AFFFFFF)
    val TabBar = Color(0xF5141634)
    val Text = Color(0xFFF5F7FF)
    val TextMuted = Color(0xFFA3A9C9)
    val TextFaint = Color(0xFF6E7499)
    val Gold = Color(0xFFFFC857)
    val GoldDeep = Color(0xFFF59E0B)
    val OnGold = Color(0xFF3A2600)
    val Danger = Color(0xFFF87171)

    val background = Brush.verticalGradient(listOf(Night, NightMid, NightEnd))
}

val Game.primary: Color get() = Color(colors.first)
val Game.secondary: Color get() = Color(colors.second)
val Game.brush: Brush get() = Brush.linearGradient(listOf(primary, secondary))

object Dimens {
    val gutter = 16.dp
    val cardRadius = 24.dp
    val cardShape = RoundedCornerShape(cardRadius)
    /** Leaves room under scrolling content for the floating tab bar. */
    val bottomBarClearance = 110.dp
}

object LottoType {
    val title = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = LottoColors.Text)
    val heading = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LottoColors.Text)
    val body = TextStyle(fontSize = 15.sp, lineHeight = 22.sp, color = LottoColors.Text)
    val caption = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, color = LottoColors.TextMuted)
    val label = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = LottoColors.TextMuted,
    )
}

@Composable
fun LuckyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = LottoColors.Gold,
            onPrimary = LottoColors.OnGold,
            background = LottoColors.Night,
            surface = LottoColors.NightMid,
            onSurface = LottoColors.Text,
            onBackground = LottoColors.Text,
            error = LottoColors.Danger,
        ),
        typography = Typography(),
        content = content,
    )
}
