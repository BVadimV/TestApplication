package com.test.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.*
import androidx.annotation.RequiresApi
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var savedUrl: String = "https://navsegda.net/"
    private lateinit var prefs: SharedPreferences

    companion object{
        private const val FILECHOOSER_RESULTCODE: Int = 1
        private const val URL_TAG = "url"
        private const val APP_PREF = "mySettings"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE)

        webView = findViewById(R.id.webView)
        webView.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
        }
        webView.webViewClient = object : WebViewClient() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())

                val currentUrl: String? = view?.url
                prefs.edit().putString(URL_TAG, currentUrl).apply()

                return true
            }
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)

                val currentUrl: String? = url
                prefs.edit().putString(URL_TAG, currentUrl).apply()

                return true
            }
        }

        if (prefs.contains(URL_TAG)) savedUrl = prefs.getString(URL_TAG, null).toString()
        webView.loadUrl(savedUrl)

    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    // попытка создать захват и загрузку фото из галереи, ниже
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var bitMap: Bitmap? = null

        when (requestCode) {
            FILECHOOSER_RESULTCODE -> {
                if (resultCode == RESULT_OK){
                    var selectImage: Uri? = data?.data
                    try {
                        bitMap = MediaStore.Images.Media.getBitmap(contentResolver, selectImage)
                    } catch (e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}