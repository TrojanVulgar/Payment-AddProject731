package net.makemoney.android.data.mappers

import net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem
import net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem
import net.makemoney.android.data.responses.WithdrawalInventoryResponse
import net.makemoney.android.data.responses.WithdrawalNewCardsResponse
import net.makemoney.android.utils.CardBackgroundProvider


class WithdrawalCardsMapper {

    fun transformToNewCards(data: List<net.makemoney.android.data.responses.WithdrawalNewCardsResponse.WithdrawalMethods>): List<net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem> = data.map {
        net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem(it.id, it.title, CardBackgroundProvider.getWithdrawalBg(it.id), CardBackgroundProvider.getWithdrawalLogo(it.id))
    }

    fun transformToInventoryCards(data: List<net.makemoney.android.data.responses.WithdrawalInventoryResponse>): List<net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem> = data.map {
        net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem(it.method_id, it.ud, it.method_name, CardBackgroundProvider.getWithdrawalBg(it.method_id), CardBackgroundProvider.getWithdrawalLogo(it.method_id), it.amount)
    }
}