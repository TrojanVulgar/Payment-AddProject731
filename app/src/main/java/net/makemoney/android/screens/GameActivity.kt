package net.makemoney.android.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_games.*
import net.makemoney.android.R
import net.makemoney.android.data.models.entertainment.GameTaskItem
import net.makemoney.android.skeleton.EmptyPresenter
import net.makemoney.android.skeleton.activity.BaseActivity


class GameActivity : BaseActivity<EmptyPresenter>() {

    private lateinit var gameItem: net.makemoney.android.data.models.entertainment.GameTaskItem

    override fun getLayoutId(): Int = R.layout.activity_games

    override fun createPresenter(): EmptyPresenter = EmptyPresenter(this)

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews() {
        gameItem = intent.getParcelableExtra(EXTRA_GAME_ITEM) as net.makemoney.android.data.models.entertainment.GameTaskItem
        webView.settings.javaScriptEnabled = true
        webView.isFocusableInTouchMode = false

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
    }

    companion object {
        const val RC_GAME_ACTIVITY = 4953
        const val EXTRA_GAME_ITEM = "extra.game.item"
    }
}