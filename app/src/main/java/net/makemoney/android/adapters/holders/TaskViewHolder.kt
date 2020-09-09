package net.makemoney.android.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_task.view.*
import net.makemoney.android.R
import net.makemoney.android.data.models.entertainment.GameTaskItem
import net.makemoney.android.data.models.entertainment.QuizTaskItem
import net.makemoney.android.data.models.tasks.OwnTaskItem
import net.makemoney.android.data.models.tasks.PartnerTaskItem
import net.makemoney.android.data.models.tasks.VideoTaskItem
import net.makemoney.android.extensions.*


class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvAward = view.tvTaskAward
    private val ivLogo = view.ivTaskLogo
    private val tvTitle = view.tvTaskNumber
    private val btnStart = view.btnTaskStart
    private val contentBackground = view.contentBackground

    fun bindNewTask(item: net.makemoney.android.data.models.tasks.OwnTaskItem) {
        with(item) {
            tvTitle.text = title
            ivLogo.load(imageUrl)
            tvAward.visible()
            tvAward.text = award.asPrice(true)
            btnStart.text = getString(R.string.tasks_btn_title_start)
        }
    }

    fun bindActiveTask(item: net.makemoney.android.data.models.tasks.OwnTaskItem) {
        with(item) {
            tvTitle.text = title
            ivLogo.load(imageUrl)
            tvAward.visible()
            tvAward.text = dailyAward.asPrice(true)
            btnStart.text = getString(R.string.tasks_btn_title_continue)
        }
    }

    fun bindPartnerTask(item: net.makemoney.android.data.models.tasks.PartnerTaskItem) {
        with(item) {
            tvTitle.text = title
            ivLogo.load(imageUrl)
            tvAward.gone()
        }
    }

    fun bindVideoTask(item: net.makemoney.android.data.models.tasks.VideoTaskItem) {
        with(item) {
            tvTitle.text = title
            tvAward.visible()
            tvAward.text = award.asPrice(true)
            ivLogo.load(imageUrl)
            btnStart.text = getString(R.string.video_btn_title)
        }
    }

    fun bindGameTask(item: net.makemoney.android.data.models.entertainment.GameTaskItem) {
        with(item) {
            tvTitle.text = title
            tvAward.visible()
            tvAward.gone()
            ivLogo.load(imageUrl)
            btnStart.text = getString(R.string.video_btn_title)
        }
    }

    fun bindQuizTask(item: net.makemoney.android.data.models.entertainment.QuizTaskItem) {
        with(item) {
            tvTitle.text = title
            tvAward.visible()
            tvAward.gone()
            ivLogo.load(imageUrl)
            btnStart.text = getString(R.string.video_btn_title)
        }
    }
}