// About screen: the trust screen. Explains how numbers are generated,
// what "Smart" and "Fresh" picks do (and don't do), privacy and settings.
package com.magzz.luckylottery.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.magzz.luckylottery.data.GAMES
import com.magzz.luckylottery.data.Settings
import com.magzz.luckylottery.data.formatOdds
import com.magzz.luckylottery.ui.components.GlassCard
import com.magzz.luckylottery.ui.components.rememberHaptics
import com.magzz.luckylottery.ui.theme.LottoColors
import com.magzz.luckylottery.ui.theme.LottoType
import com.magzz.luckylottery.ui.theme.primary

@Composable
fun AboutScreen(
    settings: Settings,
    onSettingsChange: ((Settings) -> Settings) -> Unit,
    contentPadding: PaddingValues,
) {
    val haptics = rememberHaptics(settings.haptics)
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val version = remember(context) {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: ""
    }

    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            Text("About", style = LottoType.title, modifier = Modifier.padding(top = 16.dp))
        }

        item {
            Card(Icons.Outlined.Shield, "How your numbers are made") {
                Text(
                    "Every number comes from your phone's cryptographically secure random " +
                        "generator (the same kind used for encryption), with unbiased sampling. " +
                        "That means every number has exactly the same chance of being picked, every time.",
                    style = LottoType.body.copy(color = LottoColors.TextMuted),
                )
            }
        }

        item {
            Card(Icons.Outlined.AutoAwesome, "Smart picks") {
                Text(
                    "When Smart picks is on, we avoid patterns lots of people play: " +
                        "runs of 3 or more consecutive numbers, evenly spaced sequences, and " +
                        "lines made up entirely of numbers 31 or under (common because of " +
                        "birthdays).",
                    style = LottoType.body.copy(color = LottoColors.TextMuted),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                NotBoldSentence(
                    prefix = "This does ",
                    bold = "not",
                    suffix = " improve your chances of winning. It only reduces the chance " +
                        "you'd have to share a jackpot with other winners if your numbers " +
                        "come up.",
                )
            }
        }

        item {
            Card(Icons.Outlined.Refresh, "Fresh picks") {
                Text(
                    "When Fresh picks is on, we avoid repeating any line you've " +
                        "generated before, and keep new lines from sharing more than 2 main " +
                        "numbers with your last 10 lines for that game.",
                    style = LottoType.body.copy(color = LottoColors.TextMuted),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(
                    "Again, this is purely a preference. It has no effect on your " +
                        "odds of winning.",
                    style = LottoType.body.copy(color = LottoColors.TextMuted),
                )
            }
        }

        item {
            Card(Icons.Outlined.BarChart, "Your odds") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GAMES.forEachIndexed { i, game ->
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(game.primary))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    game.name,
                                    style = LottoType.body.copy(fontWeight = FontWeight.Bold),
                                )
                            }
                            Text(game.formatLong, style = LottoType.caption)
                            Text("Draws: ${game.drawDays}", style = LottoType.caption)
                            Text(
                                "Jackpot odds: ${formatOdds(game.jackpotOdds)}",
                                style = LottoType.body.copy(color = LottoColors.Gold, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(top = 2.dp),
                            )
                            if (i != GAMES.lastIndex) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                        .size(1.dp)
                                        .background(LottoColors.Border),
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(Icons.Outlined.Lock, "Privacy") {
                Text(
                    "Lucky Lottery works completely offline. There's no account, no ads, " +
                        "and no tracking. We don't collect any data. Your history is " +
                        "stored only on this device.",
                    style = LottoType.body.copy(color = LottoColors.TextMuted),
                )
            }
        }

        item {
            Card(Icons.Outlined.Tune, "Settings") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Haptics", style = LottoType.body)
                        Text("Buzzy feedback when you tap and toggle", style = LottoType.caption)
                    }
                    Switch(
                        checked = settings.haptics,
                        onCheckedChange = { v ->
                            haptics.tick()
                            onSettingsChange { it.copy(haptics = v) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LottoColors.OnGold,
                            checkedTrackColor = LottoColors.Gold,
                            checkedBorderColor = LottoColors.Gold,
                            uncheckedThumbColor = LottoColors.TextMuted,
                            uncheckedTrackColor = LottoColors.Surface,
                            uncheckedBorderColor = LottoColors.Border,
                        ),
                        modifier = Modifier.semantics {
                            role = Role.Switch
                            contentDescription = "Toggle haptics"
                        },
                    )
                }
            }
        }

        item {
            Card(Icons.Outlined.ErrorOutline, "Play responsibly") {
                Text(
                    "You must be 18 or over to play the UK National Lottery. If gambling " +
                        "stops being fun, help is available.",
                    style = LottoType.body.copy(color = LottoColors.TextMuted),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                LinkRow("BeGambleAware.org") {
                    runCatching { uriHandler.openUri("https://www.begambleaware.org") }
                }
                LinkRow("National Gambling Helpline: 0808 802 0133") {
                    runCatching {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:08088020133")),
                        )
                    }
                }
            }
        }

        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            ) {
                Text(
                    "Lucky Lottery v$version",
                    style = LottoType.caption.copy(color = LottoColors.TextFaint),
                    textAlign = TextAlign.Center,
                )
                Text(
                    "Not affiliated with The National Lottery, Allwyn or EuroMillions. " +
                        "For entertainment only.",
                    style = LottoType.caption.copy(color = LottoColors.TextFaint),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun Card(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    GlassCard {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
            Icon(icon, contentDescription = null, tint = LottoColors.Gold, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(title, style = LottoType.heading)
        }
        content()
    }
}

@Composable
private fun NotBoldSentence(prefix: String, bold: String, suffix: String) {
    val muted = LottoColors.TextMuted
    val strong = LottoColors.Text
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(color = muted)) { append(prefix) }
            withStyle(SpanStyle(color = strong, fontWeight = FontWeight.ExtraBold)) { append(bold) }
            withStyle(SpanStyle(color = muted)) { append(suffix) }
        },
        style = LottoType.body,
    )
}

@Composable
private fun LinkRow(label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                onClick()
            }
            .semantics {
                role = Role.Button
                contentDescription = label
            }
            .padding(vertical = 4.dp),
    ) {
        Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null, tint = LottoColors.Gold, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = LottoType.body.copy(color = LottoColors.Gold))
    }
}
