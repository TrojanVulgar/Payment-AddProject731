package net.makemoney.android.mvp.presenters

import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.mappers.DetailsMapper
import net.makemoney.android.data.responses.BalanceDetailsResponse
import net.makemoney.android.mvp.contracts.BalanceContract

class BalancePresenter(override val view: BalanceContract.View) : BalanceContract.Presenter {

    private val mapper = net.makemoney.android.data.mappers.DetailsMapper()

    override fun getBalanceDetails() = net.makemoney.android.api.RestClient.api.getBalanceDetails().enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.BalanceDetailsResponse>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.BalanceDetailsResponse) {
            view.retrieveBalanceDetails(mapper.transformToBalance(body), body.balance)
        }
    })
}