package net.makemoney.android.mvp.contracts

import net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem
import net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem
import net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView


interface WithdrawalContract {

    interface View : BaseView {
        fun retrieveNewCards(newCards: List<net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem>, nominals: List<Float>, rate: Float)

        fun retrieveInventoryCards(inventoryCards: List<net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem>)

        fun cardBought()

        fun cardUsed()
    }

    interface Presenter : BasePresenter<View> {
        fun getNewCards()

        fun getInventoryCards()

        fun buyCard(card: net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem, nominal: Float)

        fun useCard(card: net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem)
    }

}