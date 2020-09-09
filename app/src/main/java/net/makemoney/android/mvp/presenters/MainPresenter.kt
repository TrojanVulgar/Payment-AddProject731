package net.makemoney.android.mvp.presenters

import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.responses.EmptyResponse
import net.makemoney.android.mvp.contracts.MainContract
import timber.log.Timber


class MainPresenter(override val view: MainContract.View) : MainContract.Presenter {

    override fun getBalance() = net.makemoney.android.api.RestClient.api.getBalance().enqueue(object : net.makemoney.android.api.DefaultCallback<Float>(view) {
        override fun onResponse(body: Float) {
            view.retrieveBalance(body)
        }
    })

    override fun sendFCMToken(token: String) = net.makemoney.android.api.RestClient.api.setFCMToken(token).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.EmptyResponse>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.EmptyResponse) {
            Timber.i("FCM token successfully send")
        }
    })
}