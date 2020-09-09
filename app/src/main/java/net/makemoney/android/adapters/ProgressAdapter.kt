package net.makemoney.android.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.makemoney.android.R
import net.makemoney.android.adapters.holders.LoadingHolder
import net.makemoney.android.adapters.holders.ProgressViewHolder
import net.makemoney.android.data.models.details.DetailsItem
import net.makemoney.android.data.models.details.ProgressItem
import net.makemoney.android.extensions.inflate
import net.makemoney.android.utils.pagination.PaginationAdapter


class ProgressAdapter(private val items: ArrayList<net.makemoney.android.data.models.details.DetailsItem>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), PaginationAdapter<net.makemoney.android.data.models.details.DetailsItem> {

    override fun getItemViewType(position: Int): Int = when {
        items[position].id < 0 -> R.layout.item_loading_holder
        else -> R.layout.item_details_progress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_loading_holder -> net.makemoney.android.adapters.holders.LoadingHolder(parent.inflate(R.layout.item_loading_holder))
        R.layout.item_details_progress -> net.makemoney.android.adapters.holders.ProgressViewHolder(parent.inflate(R.layout.item_details_progress))
        else -> throw IllegalArgumentException("Wrong active task item type")
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is net.makemoney.android.adapters.holders.LoadingHolder -> Unit
        is net.makemoney.android.adapters.holders.ProgressViewHolder -> holder.bind(items[position] as net.makemoney.android.data.models.details.ProgressItem)
        else -> throw IllegalArgumentException("Wrong active task holder type")
    }

    override fun addLoadingFooter() {
            val holderPosition = items.size
            items.add(net.makemoney.android.data.models.details.DetailsItem(-1))
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

    override fun addItems(newItems: List<net.makemoney.android.data.models.details.DetailsItem>) {
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
