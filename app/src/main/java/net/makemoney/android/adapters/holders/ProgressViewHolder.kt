package net.makemoney.android.adapters.holders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.lzyzsd.circleprogress.DonutProgress
import kotlinx.android.synthetic.main.item_details_progress.view.*
import net.makemoney.android.data.models.details.ProgressItem
import net.makemoney.android.extensions.appContext
import net.makemoney.android.extensions.getColor


class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvLevelTitle: TextView = view.tv_item_progress_title
    private val tvCurrentLevel: TextView = view.tv_item_progress_current_level
    private val tvTasksCompleted: TextView = view.tv_item_progress_tasks_completed
    private val ivTypeTask: ImageView = view.iv_item_progress_center_image
    private val progress: DonutProgress = view.pb_item_progress

    fun bind(item: net.makemoney.android.data.models.details.ProgressItem) = with(item) {
        tvLevelTitle.text = title
        tvCurrentLevel.text = level.toString()
        val tasksCompleted = "$currentProgress/$maxProgress"
        tvTasksCompleted.text = tasksCompleted
        progress.progress = percent.toFloat()
        ivTypeTask.setImageResource(icon)
        ivTypeTask.imageTintList = ContextCompat.getColorStateList(appContext, color)
        progress.finishedStrokeColor = color
        tvCurrentLevel.background = ContextCompat.getDrawable(appContext, levelShape)
        tvCurrentLevel.setTextColor(getColor(color))
    }
}