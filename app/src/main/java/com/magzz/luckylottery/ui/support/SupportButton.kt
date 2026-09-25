package com.magzz.luckylottery.ui.support

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.magzz.luckylottery.ui.theme.LottoColors

/**
 * The heart in the top-right corner of the Generate screen.
 *
 * A 22dp glyph inside [IconButton]'s 48dp touch target. Tinted muted rather than
 * gold so it stays quiet furniture, not a call to action.
 */
@Composable
fun SupportButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            HeartOutline,
            contentDescription = SupportCopy.HEART_DESCRIPTION,
            modifier = Modifier.size(22.dp),
            tint = LottoColors.TextMuted,
        )
    }
}
