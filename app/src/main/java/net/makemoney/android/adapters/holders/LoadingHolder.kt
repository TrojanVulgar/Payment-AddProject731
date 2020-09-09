package net.makemoney.android.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.item_loading_holder.view.*

/**
 * This ViewHolder used when items are fetching from server or database
 */
class LoadingHolder(val view: View) : RecyclerView.ViewHolder(view) {

    private val item: FrameLayout = view.loading_item

    fun changeSize(height: Int? = null, width: Int? = null) {
        val params = item.layoutParams
        if (height != null)
            params.height = height
        if (width != null)
            params.width = width
        item.layoutParams = params
    }
}