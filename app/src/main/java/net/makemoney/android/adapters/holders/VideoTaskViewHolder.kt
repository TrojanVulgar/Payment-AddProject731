package net.makemoney.android.adapters.holders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_video.view.*
import net.makemoney.android.data.models.tasks.VideoTaskItem
import net.makemoney.android.extensions.appContext
import net.makemoney.android.extensions.asPrice


class VideoTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val ivBackground = view.ivBackground
    private val tvPartnerTitle = view.tvVideoTitle
    private val tvAward = view.tvRewarded

    fun bind(item: net.makemoney.android.data.models.tasks.VideoTaskItem) {
        with(item) {
            ivBackground.background = ContextCompat.getDrawable(appContext, background)
            tvPartnerTitle.text = title
            tvAward.text = award.asPrice(true)
        }
    }
}