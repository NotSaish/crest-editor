package com.cosmic.ui.screens.home

import androidx.compose.runtime.Composable
import com.cosmic.ui.screens.filemanager.FileManagerScreen

@Composable
fun HomeScreen(
    onOpenFile: (String) -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenGit: () -> Unit,
    onOpenSettings: () -> Unit
) {
    FileManagerScreen(
        onOpenFile = onOpenFile,
        onOpenTerminal = onOpenTerminal,
        onOpenSettings = onOpenSettings
    )
}
