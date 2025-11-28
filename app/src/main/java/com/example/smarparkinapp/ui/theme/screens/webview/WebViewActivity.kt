package com.example.smarparkinapp.ui.theme.screens.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smarparkinapp.R

class WebViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_JWT = "EXTRA_JWT"
        const val EXTRA_MODE = "EXTRA_MODE" // "reservas" or "pago"
        const val EXTRA_RESERVA_ID = "EXTRA_RESERVA_ID"
        const val EXTRA_PARKING_ID = "EXTRA_PARKING_ID"
    }

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webview)
        setupWebView()

        val jwt = intent.getStringExtra(EXTRA_JWT) ?: ""
        val mode = intent.getStringExtra(EXTRA_MODE) ?: "reservas"
        val reservaId = intent.getStringExtra(EXTRA_RESERVA_ID)
        val parkingId = intent.getStringExtra(EXTRA_PARKING_ID)

        // URL corregida - quita el "https://" duplicado
        val backend = "http://10.0.2.2:8000" // Para emulador

        // Configurar cookies
        setupCookies(backend, jwt)

        // Cargar URL
        val urlToLoad = buildUrl(backend, mode, reservaId, parkingId)
        webView.loadUrl(urlToLoad)
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccess = true

        // Optimizaciones móviles
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    when {
                        url.startsWith("yape://") || url.startsWith("plin://") -> {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                return true
                            } catch (e: Exception) {
                                // Continuar en webview si no hay app
                            }
                        }
                        url.startsWith("tel:") -> {
                            try {
                                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(url)))
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
                injectMobileStyles()
            }
        }

        // Bridge JavaScript
        webView.addJavascriptInterface(AndroidBridge(), "Android")
    }

    private fun setupCookies(backend: String, jwt: String) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true)
        }

        if (jwt.isNotBlank()) {
            val cookieValue = "jwt-auth=$jwt; Path=/;"
            cookieManager.setCookie(backend, cookieValue)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.flush()
            }
        }
    }

    private fun buildUrl(baseUrl: String, mode: String, reservaId: String?, parkingId: String?): String {
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

    private fun injectMobileStyles() {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(css, null)
        }
    }

    inner class AndroidBridge {
        @JavascriptInterface
        fun onPaymentCreated(json: String) {
            runOnUiThread {
                Toast.makeText(this@WebViewActivity, "Pago creado", Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun onPaymentStatus(status: String, reservationId: String? = null) {
            runOnUiThread {
                Toast.makeText(this@WebViewActivity, "Estado pago: $status", Toast.LENGTH_LONG).show()
                if (status == "pagado" || status == "completed") {
                    val resultIntent = Intent().apply {
                        reservationId?.let { putExtra("RESERVATION_ID", it) }
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }

        @JavascriptInterface
        fun closeWebView() {
            runOnUiThread {
                finish()
            }
        }

        @JavascriptInterface
        fun showToast(message: String) {
            runOnUiThread {
                Toast.makeText(this@WebViewActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}