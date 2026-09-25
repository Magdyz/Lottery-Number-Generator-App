package com.magzz.luckylottery

import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.magzz.luckylottery.ui.components.rememberHaptics
import com.magzz.luckylottery.ui.screens.AboutScreen
import com.magzz.luckylottery.ui.screens.GenerateScreen
import com.magzz.luckylottery.ui.screens.HistoryScreen
import com.magzz.luckylottery.ui.theme.Dimens
import com.magzz.luckylottery.ui.theme.LottoColors
import com.magzz.luckylottery.ui.theme.LuckyTheme

private enum class Tab(val label: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    Generate("Generate", Icons.Outlined.AutoAwesome, Icons.Filled.AutoAwesome),
    History("History", Icons.Outlined.History, Icons.Filled.History),
    About("About", Icons.Outlined.VerifiedUser, Icons.Filled.VerifiedUser),
}

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The app is always dark, so system bar icons are always light.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            LuckyTheme { App(vm) }
        }
    }
}

@Composable
private fun App(vm: AppViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(Tab.Generate) }
    // Keeps each tab's remembered state (scroll position, the batch on screen)
    // alive while another tab is showing.
    val tabStates = rememberSaveableStateHolder()
    val haptics = rememberHaptics(state.settings.haptics)
    val contentPadding = PaddingValues(
        start = Dimens.gutter,
        end = Dimens.gutter,
        bottom = Dimens.bottomBarClearance,
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(LottoColors.background),
    ) {
        AnimatedContent(
            targetState = tab,
            transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
            label = "tab",
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        ) { current ->
            tabStates.SaveableStateProvider(current.name) {
                when (current) {
                    Tab.Generate -> GenerateScreen(
                        state = state,
                        onGenerate = vm::generate,
                        onToggleSaved = vm::toggleSaved,
                        onSettingsChange = vm::updateSettings,
                        onOpenAbout = { tab = Tab.About },
                        contentPadding = contentPadding,
                    )
                    Tab.History -> HistoryScreen(
                        state = state,
                        onToggleSaved = vm::toggleSaved,
                        onRemove = vm::remove,
                        onClearHistory = vm::clearHistory,
                        contentPadding = contentPadding,
                    )
                    Tab.About -> AboutScreen(
                        settings = state.settings,
                        onSettingsChange = vm::updateSettings,
                        contentPadding = contentPadding,
                    )
                }
            }
        }

        TabBar(
            selected = tab,
            onSelect = {
                if (it != tab) haptics.tick()
                tab = it
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
        )
    }
}

@Composable
private fun TabBar(selected: Tab, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    val pill = RoundedCornerShape(50)
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .shadow(16.dp, pill)
            .clip(pill)
            .background(LottoColors.TabBar)
            .border(1.dp, LottoColors.Border, pill)
            .padding(6.dp),
    ) {
        Tab.entries.forEach { t ->
            val isSelected = t == selected
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(pill)
                    .background(if (isSelected) LottoColors.Gold else LottoColors.TabBar)
                    .clickable { onSelect(t) }
                    .semantics {
                        role = Role.Tab
                        this.selected = isSelected
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Icon(
                    imageVector = if (isSelected) t.selectedIcon else t.icon,
                    contentDescription = t.label,
                    tint = if (isSelected) LottoColors.OnGold else LottoColors.TextMuted,
                    modifier = Modifier.size(20.dp),
                )
                AnimatedVisibility(visible = isSelected) {
                    Row {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            t.label,
                            color = LottoColors.OnGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}
