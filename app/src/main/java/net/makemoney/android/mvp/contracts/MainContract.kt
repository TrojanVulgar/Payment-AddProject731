package net.makemoney.android.mvp.contracts

import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView


interface MainContract {

    interface View : BaseView {
        fun retrieveBalance(balance: Float)
    }

    interface Presenter : BasePresenter<View> {
        fun getBalance()

        fun sendFCMToken(token: String)
    }

}