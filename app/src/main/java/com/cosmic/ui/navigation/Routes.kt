package com.cosmic.ui.navigation

import java.net.URLEncoder
import java.net.URLDecoder

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val EDITOR = "editor/{filePath}"
    const val TERMINAL = "terminal"
    const val GIT = "git"
    const val SETTINGS = "settings"

    fun editor(filePath: String): String {
        val encoded = java.net.URLEncoder.encode(filePath, "UTF-8")
        return "editor/$encoded"
    }
}

