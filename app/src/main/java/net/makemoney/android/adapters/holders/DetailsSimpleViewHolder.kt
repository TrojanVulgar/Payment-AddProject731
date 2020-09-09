package net.makemoney.android.adapters.holders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_details_simple_info.view.*
import net.makemoney.android.R
import net.makemoney.android.data.models.details.DetailsSimpleItem
import net.makemoney.android.data.models.referral.DetailsType
import net.makemoney.android.extensions.appContext


class DetailsSimpleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvLevelTitle: TextView = view.tv_item_details_simple_title
    private val tvTasksConditional: TextView = view.tv_item_details_simple_tasks_conditional
    private val ivTypeTask: ImageView = view.iv_item_details_simple_center_image
    private val ivShape: View = view.view_item_details_simple

    fun bind(item: net.makemoney.android.data.models.details.DetailsSimpleItem) = with(item) {
        tvLevelTitle.text = title
        when (type) {
            net.makemoney.android.data.models.referral.DetailsType.OTHER -> tvTasksConditional.text = conditional
            net.makemoney.android.data.models.referral.DetailsType.CURRENCY -> {
                tvTasksConditional.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_coin, 0)
                tvTasksConditional.text = conditional
            }
        }
        ivTypeTask.setImageResource(icon)
        ivTypeTask.imageTintList = ContextCompat.getColorStateList(appContext, color)
        ivShape.background = ContextCompat.getDrawable(appContext, shape)
    }
}