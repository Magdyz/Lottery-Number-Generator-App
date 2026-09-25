@file:OptIn(ExperimentalMaterial3Api::class)

package com.magzz.luckylottery.ui.support

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.magzz.luckylottery.ui.theme.LottoColors
import com.magzz.luckylottery.ui.theme.LottoType

/**
 * What opens when the heart is pressed. Only ever shown on request, never after
 * generating numbers or on launch.
 */
@Composable
fun SupportSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LottoColors.NightMid,
        dragHandle = { BottomSheetDefaults.DragHandle(color = LottoColors.TextFaint) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // landscape / small phones are short
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                HeartOutline,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                tint = LottoColors.Gold,
            )

            Spacer(Modifier.height(18.dp))

            Column(modifier = Modifier.widthIn(max = 460.dp)) {
                Text(
                    SupportCopy.TITLE,
                    style = LottoType.heading,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    SupportCopy.BODY,
                    style = LottoType.body.copy(color = LottoColors.TextMuted),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(28.dp))

                SupportAction(
                    icon = Icons.Outlined.StarOutline,
                    title = SupportCopy.REVIEW_TITLE,
                    body = SupportCopy.REVIEW_BODY,
                    onClick = {
                        SupportLinks.openStoreListing(context)
                        onDismiss()
                    },
                )

                if (SupportLinks.isDonationConfigured) {
                    Spacer(Modifier.height(12.dp))
                    SupportAction(
                        icon = Icons.Outlined.LocalCafe,
                        title = SupportCopy.DONATE_TITLE,
                        body = SupportCopy.DONATE_BODY,
                        onClick = {
                            SupportLinks.openDonation(context)
                            onDismiss()
                        },
                    )
                }
            }
        }
    }
}

/** A row rather than a button: neither option is the default, and doing neither is fine. */
@Composable
private fun SupportAction(
    icon: ImageVector,
    title: String,
    body: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        shape = shape,
        color = LottoColors.Surface,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, LottoColors.Border, shape)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = LottoColors.Gold)
            Spacer(Modifier.size(16.dp))
            Column {
                Text(title, style = LottoType.heading.copy(fontSize = LottoType.body.fontSize))
                Text(body, style = LottoType.caption.copy(fontSize = LottoType.body.fontSize * 0.93f))
            }
        }
    }
}
