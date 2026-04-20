package com.cosmic.ui.screens.filemanager

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0L,
    val lastModified: Long = 0L
)

class FileManagerViewModel : ViewModel() {

    private val _currentPath = MutableStateFlow("/storage/emulated/0")
    val currentPath: StateFlow<String> = _currentPath

    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    val files: StateFlow<List<FileItem>> = _files

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadFiles("/sdcard")
    }

    fun loadFiles(path: String) {
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) {
            _error.value = "Cannot open folder"
            return
        }

        _currentPath.value = path
        _files.value = dir.listFiles()
            ?.map { file ->
                FileItem(
                    name = file.name,
                    path = file.absolutePath,
                    isDirectory = file.isDirectory,
                    size = if (file.isFile) file.length() else 0L,
                    lastModified = file.lastModified()
                )
            }
            ?.sortedWith(compareByDescending<FileItem> { it.isDirectory }
                .thenBy { it.name.lowercase() })
            ?: emptyList()
    }

    fun navigateUp() {
        val parent = File(_currentPath.value).parent
        if (parent != null) loadFiles(parent)
    }

    fun createFile(name: String) {
        val file = File(_currentPath.value, name)
        if (file.createNewFile()) {
            loadFiles(_currentPath.value)
        } else {
            _error.value = "File already exists"
        }
    }

    fun createFolder(name: String) {
        val folder = File(_currentPath.value, name)
        if (folder.mkdir()) {
            loadFiles(_currentPath.value)
        } else {
            _error.value = "Folder already exists"
        }
    }

    fun deleteFile(path: String) {
        val file = File(path)
        if (file.deleteRecursively()) {
            loadFiles(_currentPath.value)
        } else {
            _error.value = "Cannot delete"
        }
    }

    fun renameFile(path: String, newName: String) {
        val file = File(path)
        val newFile = File(file.parent, newName)
        if (file.renameTo(newFile)) {
            loadFiles(_currentPath.value)
        } else {
            _error.value = "Cannot rename"
        }
    }

    fun clearError() {
        _error.value = null
    }
}
