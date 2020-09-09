package net.makemoney.android.adapters.holders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_withdrawal_new_card.view.*
import net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem
import net.makemoney.android.extensions.appContext


class WithdrawalNewCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val ivLogo = view.ivWithdrawalLogo
    private val tvTitle = view.tvWithdrawalTitle
    private val background = view.bgNewCard

    fun bind(item: net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem) = with(item) {
        ivLogo.setImageResource(logo)
        tvTitle.text = title
        background.backgroundTintList = ContextCompat.getColorStateList(appContext, bgColor)
    }
}