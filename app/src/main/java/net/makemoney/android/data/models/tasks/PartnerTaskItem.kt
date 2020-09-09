package net.makemoney.android.data.models.tasks

import com.google.gson.annotations.SerializedName
import net.makemoney.android.R


data class PartnerTaskItem(
        override val id: Int,
        val title: String,
        @SerializedName("image_url")
        val imageUrl: String,
        val description: String,
        val award: Float,
        val background: Int = R.drawable.bg_partner_card_1
) : net.makemoney.android.data.models.tasks.TaskItem()