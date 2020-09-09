package net.makemoney.android.mvp.presenters

import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.mappers.DetailsMapper
import net.makemoney.android.data.responses.ReferralResponse
import net.makemoney.android.mvp.contracts.ReferralContract


class ReferralPresenter(override val view: ReferralContract.View) : ReferralContract.Presenter {

    private val mapper = net.makemoney.android.data.mappers.DetailsMapper()

    override fun getReferralItems() = net.makemoney.android.api.RestClient.api.getReferralBalanceDetails().enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.ReferralResponse>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.ReferralResponse) {
            view.retrieveReferralItems(mapper.transformToReferral(body))
        }
    })
}