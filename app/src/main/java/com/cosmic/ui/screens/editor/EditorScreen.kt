package com.cosmic.ui.screens.editor

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cosmic.ui.theme.*
import java.io.File

// Syntax highlight colors
val KeywordColor = Color(0xFFCC99CD)
val StringColor = Color(0xFF7EC699)
val CommentColor = Color(0xFF999999)
val NumberColor = Color(0xFFF08D49)
val FunctionColor = Color(0xFF6196CC)

fun highlightSyntax(code: String, extension: String): AnnotatedString {
    return buildAnnotatedString {
        append(code)

        val keywords = when (extension) {
            "py" -> listOf("def", "class", "import", "from", "return", "if", "else", "elif",
                "for", "while", "in", "not", "and", "or", "True", "False", "None",
                "try", "except", "finally", "with", "as", "pass", "break", "continue",
                "lambda", "yield", "global", "nonlocal", "del", "raise", "assert")
            "kt" -> listOf("fun", "val", "var", "class", "object", "interface", "when",
                "if", "else", "for", "while", "return", "import", "package", "null",
                "true", "false", "override", "private", "public", "protected", "data",
                "sealed", "companion", "by", "lazy", "in", "is", "as", "try", "catch")
            "js", "ts" -> listOf("function", "const", "let", "var", "return", "if", "else",
                "for", "while", "class", "import", "export", "from", "null", "undefined",
                "true", "false", "new", "this", "typeof", "instanceof", "try", "catch")
            "java" -> listOf("public", "private", "protected", "class", "interface", "void",
                "return", "if", "else", "for", "while", "new", "null", "true", "false",
                "static", "final", "import", "package", "try", "catch", "extends")
            else -> emptyList()
        }

        // Keywords highlight
        keywords.forEach { keyword ->
            val pattern = Regex("\\b$keyword\\b")
            pattern.findAll(code).forEach { match ->
                addStyle(
                    SpanStyle(color = KeywordColor, fontWeight = FontWeight.Bold),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }

        // Strings highlight
        Regex("\"[^\"]*\"|'[^']*'").findAll(code).forEach { match ->
            addStyle(
                SpanStyle(color = StringColor),
                match.range.first,
                match.range.last + 1
            )
        }

        // Comments highlight
        val commentPattern = when (extension) {
            "py" -> Regex("#[^\n]*")
            else -> Regex("//[^\n]*|/\\*[^*]*\\*/")
        }
        commentPattern.findAll(code).forEach { match ->
            addStyle(
                SpanStyle(color = CommentColor),
                match.range.first,
                match.range.last + 1
            )
        }

        // Numbers highlight
        Regex("\\b\\d+\\.?\\d*\\b").findAll(code).forEach { match ->
            addStyle(
                SpanStyle(color = NumberColor),
                match.range.first,
                match.range.last + 1
            )
        }
    }
}

@Composable
fun EditorScreen(
    fileName: String,
    onBack: () -> Unit
) {
    val file = remember { File(fileName) }
    val extension = remember { file.extension.lowercase() }
    val initialContent = remember { if (file.exists()) file.readText() else "" }

    var textValue by remember {
        mutableStateOf(TextFieldValue(initialContent))
    }
    var isSaved by remember { mutableStateOf(true) }

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBlack)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CosmicSurface)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CosmicWhite)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    color = CosmicWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = file.parent ?: "",
                    color = CosmicGray,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!isSaved) {
                Text("●", color = CosmicYellow, fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 4.dp))
            }

            IconButton(onClick = {
                file.writeText(textValue.text)
                isSaved = true
            }) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = "Save",
                    tint = if (isSaved) CosmicGray else CosmicPurple
                )
            }
        }

        // Editor Area
        Row(modifier = Modifier.fillMaxSize()) {

            // Line Numbers
            val lines = textValue.text.split("\n")
            Column(
                modifier = Modifier
                    .background(CosmicSurface)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .verticalScroll(verticalScroll)
            ) {
                lines.forEachIndexed { index, _ ->
                    Text(
                        text = "${index + 1}",
                        color = CosmicGray,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 20.sp
                    )
                }
            }

            // Code Editor
            BasicTextField(
                value = textValue,
                onValueChange = { new ->
                    val highlighted = highlightSyntax(new.text, extension)
                    textValue = new.copy(annotatedString = highlighted)
                    isSaved = false
                },
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScroll)
                    .horizontalScroll(horizontalScroll)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                textStyle = TextStyle(
                    color = CosmicWhite,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 20.sp
                ),
                cursorBrush = SolidColor(CosmicPurple),
            )
        }
    }
}
