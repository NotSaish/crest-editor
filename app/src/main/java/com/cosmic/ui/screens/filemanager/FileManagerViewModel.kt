package com.cosmic.ui.screens.filemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean
)

class FileManagerViewModel : ViewModel() {

    private val _currentPath =
        MutableStateFlow("/storage/emulated/0")
    val currentPath: StateFlow<String> = _currentPath

    private val _files =
        MutableStateFlow<List<FileItem>>(emptyList())
    val files: StateFlow<List<FileItem>> = _files

    private val _error =
        MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadFiles("/storage/emulated/0")
    }

    fun loadFiles(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dir = File(path)

                if (!dir.exists() || !dir.isDirectory) {
                    _error.value = "Cannot open folder"
                    return@launch
                }

                val list = dir.listFiles()
                    ?.map { file ->
                        FileItem(
                            name = file.name,
                            path = file.absolutePath,
                            isDirectory = file.isDirectory
                        )
                    }
                    ?.sortedWith(
                        compareByDescending<FileItem> { file ->
                            file.isDirectory
                        }.thenBy { file ->
                            file.name.lowercase()
                        }
                    )
                    ?: emptyList()

                _currentPath.value = path
                _files.value = list
                _error.value = null

            } catch (e: Exception) {
                _error.value = "Failed to load folder"
            }
        }
    }

    fun navigateUp() {
        File(_currentPath.value).parent?.let {
            loadFiles(it)
        }
    }

    fun showError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }
}
