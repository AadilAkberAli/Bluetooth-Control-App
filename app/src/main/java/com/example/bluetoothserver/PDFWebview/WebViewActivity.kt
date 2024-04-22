package com.example.bluetoothserver.PDFWebview

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.bluetoothserver.R

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view)
        var webView = findViewById<WebView>(R.id.webView);
        webView.webViewClient = WebViewClient()
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        val url = getPdfUrl()
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$url")
    }

    fun getPdfUrl(): String {
        return "https://mindorks.s3.ap-south-1.amazonaws.com/courses/MindOrks_Android_Online_Professional_Course-Syllabus.pdf"
    }
}