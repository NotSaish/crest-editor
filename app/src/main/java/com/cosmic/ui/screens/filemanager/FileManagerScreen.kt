@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

package com.cosmic.ui.screens.filemanager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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

    val listState = rememberLazyListState()

    Scaffold(
        containerColor = CosmicBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Cosmic Code",
                            color = CosmicWhite
                        )

                        Text(
                            text = currentPath,
                            color = CosmicGray,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CosmicBlack
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CosmicBlack)
        ) {

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {

                if (currentPath != "/storage/emulated/0") {
                    item(key = "back_button") {
                        FileRow(
                            name = "..",
                            isDirectory = true,
                            onClick = { viewModel.navigateUp() }
                        )
                    }
                }

                items(
                    items = files,
                    key = { it.path }
                ) { file ->

                    FileRow(
                        name = file.name,
                        isDirectory = file.isDirectory,
                        onClick = {
                            if (file.isDirectory) {
                                viewModel.loadFiles(file.path)
                            } else {
                                val ext = file.name
                                    .substringAfterLast(".", "")
                                    .lowercase()

                                val textExtensions = setOf(
                                    "kt", "java", "kts",
                                    "xml", "json", "txt",
                                    "md", "js", "ts",
                                    "html", "css",
                                    "py", "c", "cpp",
                                    "h", "hpp", "rs",
                                    "go", "php", "sh",
                                    "yaml", "yml"
                                )

                                if (ext in textExtensions) {
                                    onOpenFile(file.path)
                                } else {
                                    viewModel.showError("Unsupported file type")
                                }
                            }
                        }
                    )
                }
            }

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

@Composable
private fun FileRow(
    name: String,
    isDirectory: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = if (isDirectory)
                Icons.Default.Folder
            else
                Icons.Default.InsertDriveFile,
            contentDescription = null,
            tint = CosmicBlue
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = name,
            color = CosmicWhite,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
    }

    HorizontalDivider(
        color = CosmicGray.copy(alpha = 0.12f)
    )
}
