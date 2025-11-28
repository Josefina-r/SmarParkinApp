package com.example.smarparkinapp.ui.theme.screens.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewComposable(
    jwt: String = "",
    mode: String = "reservas",
    reservaId: String? = null,
    parkingId: String? = null,
    onFinish: () -> Unit = {},
    onPaymentSuccess: (reservationId: String) -> Unit = {},
    onError: (message: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val backendBaseUrl = "http://10.0.2.2:8000"
    val finalUrl = remember(jwt, mode, reservaId, parkingId) {
        buildComposableUrl(backendBaseUrl, mode, reservaId, parkingId)
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                setupComposableWebView(backendBaseUrl, jwt, finalUrl, onPaymentSuccess, onFinish, onError)
            }
        },
        modifier = modifier
    )
}

private fun WebView.setupComposableWebView(
    backend: String,
    jwt: String,
    finalUrl: String,
    onPaymentSuccess: (String) -> Unit,
    onFinish: () -> Unit,
    onError: (String) -> Unit
) {
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.allowContentAccess = true
    settings.allowFileAccess = true

    settings.loadWithOverviewMode = true
    settings.useWideViewPort = true
    settings.setSupportZoom(true)
    settings.builtInZoomControls = true
    settings.displayZoomControls = false

    webChromeClient = WebChromeClient()
    webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            url?.let {
                when {
                    it.startsWith("yape://") || it.startsWith("plin://") -> {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                            context.startActivity(intent)
                            return true
                        } catch (e: Exception) {
                            // Continuar en webview
                        }
                    }
                    it.startsWith("tel:") -> {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(it))
                            context.startActivity(intent)
                            return true
                        } catch (e: Exception) {
                            // Ignorar error
                        }
                    }
                }
            }
            return false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            injectComposableStyles()
        }
    }

    // Configurar cookies
    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(true)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        cookieManager.setAcceptThirdPartyCookies(this, true)
    }

    if (jwt.isNotBlank()) {
        val cookieValue = "jwt-auth=$jwt; Path=/;"
        cookieManager.setCookie(backend, cookieValue)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush()
        }
    }

    // Bridge JavaScript
    addJavascriptInterface(ComposableWebAppInterface(
        onPaymentSuccess = onPaymentSuccess,
        onFinish = onFinish,
        onError = onError
    ), "AndroidApp")

    loadUrl(finalUrl)
}

private fun buildComposableUrl(baseUrl: String, mode: String, reservaId: String?, parkingId: String?): String {
    return when (mode) {
        "pago" -> {
            if (!reservaId.isNullOrBlank()) {
                "$baseUrl/reservations/payment/$reservaId/"
            } else {
                "$baseUrl/reservations/payment/"
            }
        }
        "reservas" -> {
            if (!parkingId.isNullOrBlank()) {
                "$baseUrl/reservations/?parking_id=$parkingId"
            } else {
                "$baseUrl/reservations/"
            }
        }
        "mis_reservas" -> "$baseUrl/api/reservations/client/mis-reservas/"
        else -> "$baseUrl/reservations/"
    }
}

private fun injectComposableStyles() {
    val css = """
        javascript:(function() {
            var style = document.createElement('style');
            style.innerHTML = `
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    max-width: 100%;
                    overflow-x: hidden;
                    margin: 0;
                    padding: 0;
                }
                .container {
                    padding: 16px;
                }
                input, select, button {
                    font-size: 16px !important;
                    min-height: 44px !important;
                }
                .btn {
                    min-height: 44px !important;
                    display: flex !important;
                    align-items: center !important;
                    justify-content: center !important;
                }
            `;
            document.head.appendChild(style);
        })()
    """.trimIndent()

    // La inyección se maneja automáticamente en el WebViewClient
}

class ComposableWebAppInterface(
    private val onPaymentSuccess: (String) -> Unit,
    private val onFinish: () -> Unit,
    private val onError: (String) -> Unit
) {
    @JavascriptInterface
    fun onPaymentCreated(reservationData: String) {
        Toast.makeText(
            android.app.Application().applicationContext,
            "Pago creado exitosamente",
            Toast.LENGTH_SHORT
        ).show()
    }

    @JavascriptInterface
    fun onPaymentStatus(status: String, reservationId: String? = null) {
        when (status.toLowerCase()) {
            "pagado", "completed", "success", "approved" -> {
                reservationId?.let { onPaymentSuccess(it) }
                onFinish()
            }
            "failed", "error", "rechazado" -> {
                onError("Error en el proceso de pago")
            }
            "cancelled", "cancelado" -> {
                onFinish()
            }
        }
    }

    @JavascriptInterface
    fun closeWebView() {
        onFinish()
    }

    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(
            android.app.Application().applicationContext,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}