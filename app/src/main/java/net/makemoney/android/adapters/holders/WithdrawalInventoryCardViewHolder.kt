package net.makemoney.android.adapters.holders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_withdrawal_inventory_card.view.*
import net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem
import net.makemoney.android.extensions.appContext
import net.makemoney.android.extensions.toBalance


class WithdrawalInventoryCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val ivLogo = view.ivWithdrawalLogo
    private val tvTitle = view.tvWithdrawalTitle
    private val background = view.bgInventoryCard
    private val tvAmount = view.tvWithdrawalAmount

    fun bind(item: net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem) = with(item) {
        tvAmount.text = amount.toBalance()
        background.backgroundTintList = ContextCompat.getColorStateList(appContext, bgColor)
        ivLogo.setImageResource(logo)
        tvTitle.text = title
    }
}