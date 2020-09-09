package net.makemoney.android.mvp.presenters

import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.mappers.WithdrawalCardsMapper
import net.makemoney.android.data.models.withdrawal.WithdrawalBuyCardItem
import net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem
import net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem
import net.makemoney.android.data.responses.EmptyResponse
import net.makemoney.android.data.responses.WithdrawalInventoryResponse
import net.makemoney.android.data.responses.WithdrawalNewCardsResponse
import net.makemoney.android.mvp.contracts.WithdrawalContract

class WithdrawalPresenter(override val view: WithdrawalContract.View) : WithdrawalContract.Presenter {

    private val mapper = net.makemoney.android.data.mappers.WithdrawalCardsMapper()

    override fun getNewCards() = net.makemoney.android.api.RestClient.api.getWithdrawalNewCards().enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.WithdrawalNewCardsResponse>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.WithdrawalNewCardsResponse) {
            view.retrieveNewCards(mapper.transformToNewCards(body.methods), body.nominals, body.rate)
        }
    })

    override fun getInventoryCards() = net.makemoney.android.api.RestClient.api.getWithdrawalInventoryCards().enqueue(object : net.makemoney.android.api.DefaultCallback<List<net.makemoney.android.data.responses.WithdrawalInventoryResponse>>(view) {
        override fun onResponse(body: List<net.makemoney.android.data.responses.WithdrawalInventoryResponse>) {
            view.retrieveInventoryCards(mapper.transformToInventoryCards(body))
        }
    })

    override fun buyCard(card: net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem, nominal: Float) {
        val buyingCard = net.makemoney.android.data.models.withdrawal.WithdrawalBuyCardItem(nominal, card.id)
        net.makemoney.android.api.RestClient.api.buyCard(buyingCard).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.EmptyResponse>(view) {
            override fun onResponse(body: net.makemoney.android.data.responses.EmptyResponse) {
                view.cardBought()
            }
        })
    }

    override fun useCard(card: net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem) = net.makemoney.android.api.RestClient.api.useCard(card).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.EmptyResponse>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.EmptyResponse) {
            view.cardUsed()
        }
    })
}