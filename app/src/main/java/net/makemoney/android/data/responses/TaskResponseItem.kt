package net.makemoney.android.data.responses

import net.makemoney.android.extensions.defaultCardColorsIntArray
import net.makemoney.android.extensions.defaultProgressBarConditionsIntArray


data class TaskResponseItem(val id: Int,
                            val package_name: String = "",
                            val title: String = "",
                            val image_url: String = "",
                            val description: String = "",
                            val tracking_link: String? = null,
                            val duration: Int = 0,
                            val award: Float = 0f,
                            val daily_award: Float = 0f,
                            val type: Int = 0,
                            val days: Int = 0,
                            val time_delay: Int = 24,
                            val keywords: List<String>? = emptyList(),
                            val available_time: String? = "",
                            val rate_type: Int = 0,
                            val rate_keywords: List<String>? = emptyList(),
                            val pivot: net.makemoney.android.data.responses.TaskResponseItem.TaskPivot? = null
) {
    data class TaskPivot(
            val times: Int = 0,
            val failed_times: Int = 0,
            val is_available: Boolean = true,
            val is_rating_available: Int = 0,
            val cards: IntArray? = defaultCardColorsIntArray,
            val progress_bar: IntArray? = defaultProgressBarConditionsIntArray
    )
}