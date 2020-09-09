package net.makemoney.android.adapters.holders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_entertainment.view.*
import net.makemoney.android.data.models.entertainment.GameTaskItem
import net.makemoney.android.data.models.entertainment.QuizTaskItem
import net.makemoney.android.extensions.appContext
import net.makemoney.android.extensions.gone


class EntertainmentTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val ivBackground = view.ivBackground
    private val tvPartnerTitle = view.tvEntertainmentTitle
    private val tvAward = view.tvRewarded

    fun bindQuiz(item: net.makemoney.android.data.models.entertainment.QuizTaskItem) {
        with(item) {
            ivBackground.background = ContextCompat.getDrawable(appContext, background)
            tvPartnerTitle.text = title
            tvAward.gone()
        }
    }

    fun bindGame(item: net.makemoney.android.data.models.entertainment.GameTaskItem) {
        with(item) {
            ivBackground.background = ContextCompat.getDrawable(appContext, background)
            tvPartnerTitle.text = title
            tvAward.gone()
        }
    }
}