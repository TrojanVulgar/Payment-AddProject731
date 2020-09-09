package net.makemoney.android.data.responses


data class QuizzesResponse(
        val id: Int,
        val title: String,
        val image_url: String,
        val pivot: net.makemoney.android.data.responses.QuizzesResponse.Pivot
) {
    data class Pivot(
            val today_times: Int,
            val limit: Int,
            val times: Int,
            val earned: Float,
            val is_available: Boolean
    )
}