package net.makemoney.android.adapters.holders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_partner.view.*
import net.makemoney.android.data.models.tasks.PartnerTaskItem
import net.makemoney.android.extensions.appContext


class PartnerTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val ivBackground = view.ivBackground
    private val tvPartnerTitle = view.tvPartnerTitle

    fun bindTask(item: net.makemoney.android.data.models.tasks.PartnerTaskItem) {
        with(item) {
            ivBackground.background = ContextCompat.getDrawable(appContext, background)
            tvPartnerTitle.text = title
        }
    }
}