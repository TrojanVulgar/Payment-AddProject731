package net.makemoney.android.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_entertainment.view.*
import kotlinx.android.synthetic.main.item_partner.view.*
import kotlinx.android.synthetic.main.item_task.view.*
import kotlinx.android.synthetic.main.item_video.view.*
import net.makemoney.android.R
import net.makemoney.android.adapters.holders.*
import net.makemoney.android.data.models.entertainment.GameTaskItem
import net.makemoney.android.data.models.entertainment.QuizTaskItem
import net.makemoney.android.data.models.tasks.*
import net.makemoney.android.extensions.getDimens
import net.makemoney.android.extensions.inflate
import net.makemoney.android.utils.pagination.PaginationAdapter


class TasksAdapter(private val items: ArrayList<net.makemoney.android.data.models.tasks.TaskItem>,
                   private val onClick: (net.makemoney.android.data.models.tasks.TaskItem) -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), PaginationAdapter<net.makemoney.android.data.models.tasks.TaskItem> {

    var type = 0

    override fun getItemViewType(position: Int): Int = when {
        items[position].id < 0 -> R.layout.item_loading_holder
        else -> {
            when (type) {
                net.makemoney.android.data.models.tasks.TaskType.ACTIVE -> R.layout.item_task
                net.makemoney.android.data.models.tasks.TaskType.NEW -> R.layout.item_task
                net.makemoney.android.data.models.tasks.TaskType.PARTNER -> R.layout.item_partner
                net.makemoney.android.data.models.tasks.TaskType.VIDEO -> R.layout.item_video
                net.makemoney.android.data.models.tasks.TaskType.GAMES -> R.layout.item_entertainment
                net.makemoney.android.data.models.tasks.TaskType.QUIZZES -> R.layout.item_entertainment
                else -> R.layout.item_task
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_loading_holder -> net.makemoney.android.adapters.holders.LoadingHolder(parent.inflate(R.layout.item_loading_holder))
        R.layout.item_entertainment -> net.makemoney.android.adapters.holders.EntertainmentTaskViewHolder(parent.inflate(R.layout.item_entertainment))
        R.layout.item_task -> net.makemoney.android.adapters.holders.TaskViewHolder(parent.inflate(R.layout.item_task))
        R.layout.item_partner -> net.makemoney.android.adapters.holders.PartnerTaskViewHolder(parent.inflate(R.layout.item_partner))
        R.layout.item_video -> net.makemoney.android.adapters.holders.VideoTaskViewHolder(parent.inflate(R.layout.item_video))
        else -> throw IllegalArgumentException("Wrong active task item type")
    }.apply {
        when (this){
            is net.makemoney.android.adapters.holders.TaskViewHolder ->
                itemView.btnTaskStart.setOnClickListener {
                    val selectedPosition = adapterPosition
                    if (selectedPosition != RecyclerView.NO_POSITION) {
                        onClick(items[selectedPosition])
                    }
                }
            is net.makemoney.android.adapters.holders.PartnerTaskViewHolder ->
                itemView.btnPartnerStart.setOnClickListener {
                    val selectedPosition = adapterPosition
                    if (selectedPosition != RecyclerView.NO_POSITION) {
                        onClick(items[selectedPosition])
                    }
                }
            is net.makemoney.android.adapters.holders.VideoTaskViewHolder ->
                itemView.btnVideoStart.setOnClickListener {
                    val selectedPosition = adapterPosition
                    if (selectedPosition != RecyclerView.NO_POSITION) {
                        onClick(items[selectedPosition])
                    }
                }
            is net.makemoney.android.adapters.holders.EntertainmentTaskViewHolder ->
                itemView.btnEntertainmentStart.setOnClickListener {
                    val selectedPosition = adapterPosition
                    if (selectedPosition != RecyclerView.NO_POSITION) {
                        onClick(items[selectedPosition])
                    }
                }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is net.makemoney.android.adapters.holders.LoadingHolder -> holder.changeSize(getDimens(R.dimen.task_item_height))
        is net.makemoney.android.adapters.holders.PartnerTaskViewHolder -> holder.bindTask(items[position] as net.makemoney.android.data.models.tasks.PartnerTaskItem)
        is net.makemoney.android.adapters.holders.VideoTaskViewHolder -> holder.bind(items[position] as net.makemoney.android.data.models.tasks.VideoTaskItem)
        is net.makemoney.android.adapters.holders.EntertainmentTaskViewHolder -> {
            when(type) {
                net.makemoney.android.data.models.tasks.TaskType.GAMES -> holder.bindGame(items[position] as net.makemoney.android.data.models.entertainment.GameTaskItem)
                net.makemoney.android.data.models.tasks.TaskType.QUIZZES -> holder.bindQuiz(items[position] as net.makemoney.android.data.models.entertainment.QuizTaskItem)
                else -> throw IllegalArgumentException("wrong task type")
            }
        }
        is net.makemoney.android.adapters.holders.TaskViewHolder -> {
            when(type) {
                net.makemoney.android.data.models.tasks.TaskType.NEW -> holder.bindNewTask(items[position] as net.makemoney.android.data.models.tasks.OwnTaskItem)
                net.makemoney.android.data.models.tasks.TaskType.ACTIVE -> holder.bindActiveTask(items[position] as net.makemoney.android.data.models.tasks.OwnTaskItem)
                else -> throw IllegalArgumentException("wrong task type: $type")
            }
        }
        else -> throw IllegalArgumentException("Wrong active task holder type")
    }

    override fun addLoadingFooter() {
        val holderPosition = items.size
        items.add(net.makemoney.android.data.models.tasks.OwnTaskItem())
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

    override fun addItems(newItems: List<net.makemoney.android.data.models.tasks.TaskItem>) {
        clearItems()
        val from = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(from, newItems.size)
    }

    override fun clearItems() {
        if (items.isEmpty()) return
        items.clear()
        notifyDataSetChanged()
    }
}