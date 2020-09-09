package net.makemoney.android.mvp.contracts

import net.makemoney.android.data.models.details.DetailsItem
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView

interface ReferralContract {

    interface View : BaseView {
        fun retrieveReferralItems(items: List<net.makemoney.android.data.models.details.DetailsItem>)
    }

    interface Presenter : BasePresenter<View> {
        fun getReferralItems()
    }

}
