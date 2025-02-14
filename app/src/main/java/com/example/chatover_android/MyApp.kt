package com.example.chatover_android

import android.app.Application
import com.example.chatover_android.utils.Keys.APP_ID
import com.example.chatover_android.utils.Keys.APP_SIGN
import com.zegocloud.zimkit.services.ZIMKit
import java.util.Locale
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build

fun forceZIMKitToUseEnglish(context: android.content.Context) {
    val config = Configuration(context.resources.configuration)
    val locale = Locale.ENGLISH

    Locale.setDefault(locale)
    config.setLocale(locale)

    @Suppress("DEPRECATION")
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        forceZIMKitToUseEnglish(this)
        ZIMKit.initWith(this,APP_ID,APP_SIGN)
        ZIMKit.initNotifications()
    }
}