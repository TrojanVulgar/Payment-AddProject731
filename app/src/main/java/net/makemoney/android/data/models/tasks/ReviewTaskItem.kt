package net.makemoney.android.data.models.tasks

import okhttp3.MultipartBody


data class ReviewTaskItem(
        val task_id: Int,
        val screen: MultipartBody.Part
)