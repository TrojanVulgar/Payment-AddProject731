package net.makemoney.android.mvp.contracts

import net.makemoney.android.data.models.details.DetailsItem
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView


interface BalanceContract {

    interface View : BaseView {
        fun retrieveBalanceDetails(detailsItems: List<net.makemoney.android.data.models.details.DetailsItem>, balance: Float)
    }

    interface Presenter : BasePresenter<View> {
        fun getBalanceDetails()
    }
}