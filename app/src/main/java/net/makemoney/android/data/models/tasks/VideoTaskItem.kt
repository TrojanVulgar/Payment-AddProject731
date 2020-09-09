package net.makemoney.android.data.models.tasks

import net.makemoney.android.R


data class VideoTaskItem(
        override val id: Int,
        val title: String,
        val award: Float,
        val imageUrl: String,
        val isAvailable: Boolean,
        val background: Int = R.drawable.bg_video_card_1
) : net.makemoney.android.data.models.tasks.TaskItem()