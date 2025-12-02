package com.example.smarparkinapp.ui.theme.components

import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TermsWebView(
    htmlContent: String,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    val webView = rememberWebView()

    LaunchedEffect(htmlContent, searchQuery) {
        val highlightedContent = if (searchQuery.isNotEmpty()) {
            highlightTextInHtml(htmlContent, searchQuery)
        } else {
            htmlContent
        }

        webView.loadDataWithBaseURL(
            null,
            highlightedContent,
            "text/html",
            "UTF-8",
            null
        )
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    )
}

@Composable
fun rememberWebView(): WebView {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }
    }

    DisposableEffect(Unit) {
        onDispose { webView.destroy() }
    }

    return webView
}

private fun highlightTextInHtml(html: String, query: String): String {
    if (query.isBlank()) return html

    return try {
        val pattern = Regex(Regex.escape(query), RegexOption.IGNORE_CASE)
        pattern.replace(html) {
            "<mark style='background-color: #FFEB3B; padding: 2px; border-radius: 2px;'>${it.value}</mark>"
        }
    } catch (e: Exception) {
        html
    }
}
