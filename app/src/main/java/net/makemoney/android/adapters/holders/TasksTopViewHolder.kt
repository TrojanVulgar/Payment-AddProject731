package net.makemoney.android.adapters.holders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import kotlinx.android.synthetic.main.item_tasks_top_card.view.*
import net.makemoney.android.R
import net.makemoney.android.data.models.tasks.TasksTopCardItem
import net.makemoney.android.extensions.appContext
import net.makemoney.android.extensions.getString



class TasksTopViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvTitle = view.tvTasksTitle
    private val tvDescription = view.tvTasksDescription
    private val tvPrimaryButton = view.tvTasksPrimaryButton
    private val ivBackgroundCard = view.ivBackgroundImage
    private val titleGuideline = view.guideline_title

    fun bind(item: net.makemoney.android.data.models.tasks.TasksTopCardItem) {
        with(item) {
            tvTitle.text = title
            tvDescription.text = description
            tvPrimaryButton.text = getString(R.string.tasks_primary_button_title)
            ivBackgroundCard.background = ContextCompat.getDrawable(appContext, image)
        }
    }

    fun changeTitleGuideline(percent: Float, titleSize: Float = 0f) {
        titleGuideline.setGuidelinePercent(percent)
        if (titleSize != 0f) tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize)
    }
}