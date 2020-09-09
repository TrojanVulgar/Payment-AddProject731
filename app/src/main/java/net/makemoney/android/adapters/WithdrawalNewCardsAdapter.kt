package net.makemoney.android.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.makemoney.android.R
import net.makemoney.android.adapters.holders.LoadingHolder
import net.makemoney.android.adapters.holders.WithdrawalNewCardViewHolder
import net.makemoney.android.data.models.withdrawal.WithdrawalItem
import net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem
import net.makemoney.android.extensions.getDimens
import net.makemoney.android.extensions.inflate
import net.makemoney.android.utils.pagination.PaginationAdapter


class WithdrawalNewCardsAdapter(private val items: ArrayList<net.makemoney.android.data.models.withdrawal.WithdrawalItem>,
                                val onClick: (net.makemoney.android.data.models.withdrawal.WithdrawalItem) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
                                                                         PaginationAdapter<net.makemoney.android.data.models.withdrawal.WithdrawalItem>{

    override fun getItemViewType(position: Int): Int = when {
        items[position].id < 0 -> R.layout.item_loading_holder
        items[position] is net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem -> R.layout.item_withdrawal_new_card
        else -> throw IllegalArgumentException("Wrong withdrawal task type")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_loading_holder -> net.makemoney.android.adapters.holders.LoadingHolder(parent.inflate(R.layout.item_loading_holder))
        R.layout.item_withdrawal_new_card -> net.makemoney.android.adapters.holders.WithdrawalNewCardViewHolder(parent.inflate(R.layout.item_withdrawal_new_card))
        else -> throw IllegalArgumentException("Wrong withdrawal view type")
    }.apply {
        when (this) {
            is net.makemoney.android.adapters.holders.LoadingHolder -> Unit
            else -> itemView.setOnClickListener {
                val selectedPosition = adapterPosition
                if (selectedPosition != RecyclerView.NO_POSITION)
                    onClick(items[selectedPosition])
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is net.makemoney.android.adapters.holders.LoadingHolder -> holder.changeSize(getDimens(R.dimen.withdrawal_card_height), getDimens(R.dimen.withdrawal_card_width))
        is net.makemoney.android.adapters.holders.WithdrawalNewCardViewHolder -> holder.bind(items[position] as net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem)
        else -> throw IllegalArgumentException("Wrong withdrawal holder type")
    }

    override fun addLoadingFooter() {
        val holderPosition = items.size
        items.add(net.makemoney.android.data.models.withdrawal.WithdrawalItem(-1))
        notifyItemInserted(holderPosition)
    }

    override fun removeLoadingFooter() {
        val holderPosition = items.size - 1
        //removes item only if loading footer was added
        if (holderPosition >= 0) {
            items.removeAt(holderPosition)
            notifyItemRemoved(holderPosition)
        }
    }

    override fun addItems(newItems: List<net.makemoney.android.data.models.withdrawal.WithdrawalItem>) {
        removeLoadingFooter()
        val from = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(from, newItems.size)
    }

    override fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }
}