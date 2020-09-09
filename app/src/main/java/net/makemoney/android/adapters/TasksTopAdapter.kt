package net.makemoney.android.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_tasks_top_card.view.*
import net.makemoney.android.R
import net.makemoney.android.adapters.holders.TasksTopViewHolder
import net.makemoney.android.data.models.tasks.TaskType
import net.makemoney.android.data.models.tasks.TasksTopCardItem
import net.makemoney.android.extensions.inflate
import net.makemoney.android.utils.TopAdapterClicksListener
import timber.log.Timber


class TasksTopAdapter(private val items: List<net.makemoney.android.data.models.tasks.TasksTopCardItem>,
                      private val onClick: TopAdapterClicksListener) :
        RecyclerView.Adapter<net.makemoney.android.adapters.holders.TasksTopViewHolder>() {

    var type = 0

    override fun getItemViewType(position: Int): Int = R.layout.item_tasks_top_card

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): net.makemoney.android.adapters.holders.TasksTopViewHolder = when(viewType) {
        R.layout.item_tasks_top_card -> net.makemoney.android.adapters.holders.TasksTopViewHolder(parent.inflate(R.layout.item_tasks_top_card))
        else -> throw IllegalArgumentException("Wrong tasks top card view type")
    }.apply {
        itemView.tvTasksPrimaryButton.setOnClickListener {
            Timber.i("OnCLick clicked")
            val selectedPosition = adapterPosition
            if (selectedPosition != RecyclerView.NO_POSITION){
                Timber.i("OnCLick worked")
                onClick.onPrimaryBtnClick(items[selectedPosition].additionalInfo)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: net.makemoney.android.adapters.holders.TasksTopViewHolder, position: Int) {
        when (type) {
            net.makemoney.android.data.models.tasks.TaskType.QUIZZES or net.makemoney.android.data.models.tasks.TaskType.GAMES -> holder.changeTitleGuideline(0.39f, 30f)
        }
        return holder.bind(items[position])
    }
}