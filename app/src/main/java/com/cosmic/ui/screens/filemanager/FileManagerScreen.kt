@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

package com.cosmic.ui.screens.filemanager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cosmic.ui.theme.*

@Composable
fun FileManagerScreen(
    onOpenFile: (String) -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val viewModel: FileManagerViewModel = viewModel()
    val files by viewModel.files.collectAsState()
    val currentPath by viewModel.currentPath.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        containerColor = CosmicBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Cosmic Code", color = CosmicWhite)
                        Text(currentPath, color = CosmicGray, fontSize = MaterialTheme.typography.labelSmall.fontSize)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CosmicBlack)
        ) {

            if (error != null) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                // 🔥 BACK ".." ITEM
                if (currentPath.isNotEmpty() && currentPath != "/storage/emulated/0") {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { viewModel.navigateUp() }
                                )
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = CosmicBlue
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "..",
                                color = CosmicWhite
                            )
                        }
                    }
                }

                // 📁 FILE LIST
                items(files) { file ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    if (file.isDirectory) {
                                        viewModel.loadFiles(file.path)
                                    } else {
                                        onOpenFile(file.path)
                                    }
                                }
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = if (file.isDirectory)
                                Icons.Default.Folder
                            else
                                Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            tint = CosmicBlue
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = file.name,
                            color = CosmicWhite
                        )
                    }
                }
            }

            // ⚡ Bottom buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onOpenTerminal,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Terminal")
                }

                Button(
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Settings")
                }
            }
        }
    }
}
