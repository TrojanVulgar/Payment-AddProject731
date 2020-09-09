package net.makemoney.android.adapters

import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_marathon.view.*
import net.makemoney.android.R
import net.makemoney.android.data.models.marathon.MarathonItem
import net.makemoney.android.extensions.appContext
import net.makemoney.android.extensions.getColor
import net.makemoney.android.extensions.getString
import net.makemoney.android.extensions.inflate


class MarathonAdapter(private val marathon: net.makemoney.android.data.models.marathon.MarathonItem, val onClick: () -> Unit)
    : RecyclerView.Adapter<net.makemoney.android.adapters.MarathonAdapter.MarathonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : net.makemoney.android.adapters.MarathonAdapter.MarathonViewHolder {
        val holder = MarathonViewHolder(parent.inflate(R.layout.item_marathon))
        holder.itemView.setOnClickListener {
            val selectedPosition = holder.adapterPosition
            if (selectedPosition != RecyclerView.NO_POSITION) {
                onClick()
            }
        }
        return holder
    }

    override fun getItemCount(): Int = marathon.award.size

    override fun onBindViewHolder(holder: net.makemoney.android.adapters.MarathonAdapter.MarathonViewHolder, position: Int) = holder.bind(marathon)

    fun updateAfterTaken() {
        marathon.isAvailable = 0 // set marathon unavailable
        notifyItemChanged(marathon.current - 1)
    }

    inner class MarathonViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvDay: TextView = view.tvMarathonDay
        private val ivSafe: ImageView = view.ivMarathonSafe
        private val container: ConstraintLayout = view.containerMarathon
        private val containerBackground: FrameLayout = view.containerBackground
        private val tvStatus: TextView = view.tvMarathonStatus

        fun bind(item: net.makemoney.android.data.models.marathon.MarathonItem) {
            //BackgroundTint works only in api >= M
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                container.setBackgroundColor(getColor(R.color.marathon_background))
            }
            val day = adapterPosition + 1
            with(item) {
                when {
                    (day < current || (day == current && isAvailable == 0)) -> {
                        tvStatus.text = getString(R.string.marathon_done)
                        container.backgroundTintList = ContextCompat.getColorStateList(appContext, R.color.marathon_done)
                    }
                    ((day == current && isAvailable == 1) || (day == max && current == max && isAvailable == 1)) -> {
                        tvStatus.text = getString(R.string.marathon_ready)
                        container.backgroundTintList = ContextCompat.getColorStateList(appContext, R.color.marathon_ready)
                        val params = containerBackground.layoutParams as ConstraintLayout.LayoutParams
                        params.setMargins(4, 4, 4, 0)
                        containerBackground.layoutParams = params
                        containerBackground.requestLayout()
                    }
                    day - current == 1 -> {
                        tvStatus.text = getString(R.string.marathon_soon)
                        container.backgroundTintList = ContextCompat.getColorStateList(appContext, R.color.marathon_soon)
                    }
                    isAvailable == 2 -> {
                        tvStatus.text = getString(R.string.marathon_done)
                        container.backgroundTintList = ContextCompat.getColorStateList(appContext, R.color.marathon_done)
                    }
                    else -> {
                        tvStatus.text = getString(R.string.marathon_soon)
                        container.backgroundTintList = ContextCompat.getColorStateList(appContext, R.color.marathon_soon)
                    }
                }
                tvDay.text = getString(R.string.marathon_day_title, day)
                ivSafe.setImageResource(when {
                    (day < current || (day == current && isAvailable == 0)) -> R.drawable.ic_marathon_done
                    (day == current && isAvailable == 1) || (day == max && current == max && isAvailable == 1) -> R.drawable.ic_marathon_ready
                    day - current == 1 -> R.drawable.ic_marathon_soon
                    isAvailable == 2 -> R.drawable.ic_marathon_done
                    else -> R.drawable.ic_marathon_soon
                })
                if (day < current || day > current || (day == current && isAvailable == 0)) {
                    container.isEnabled = false
                    container.isClickable = false
                }
            }
            container.setOnClickListener { onClick() }
        }
    }
}