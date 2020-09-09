package net.makemoney.android.screens

import android.app.Activity
import android.os.Bundle
import android.view.View
import org.jetbrains.anko.startActivity


class SplashScreen : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        startActivity<StartActivity>()
        finish()
    }
}