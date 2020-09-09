package net.makemoney.android.data.responses


data class VideoResponse(
        val video_limit: net.makemoney.android.data.responses.VideoResponse.Limit,
        val videos: List<net.makemoney.android.data.responses.VideoResponse.VideoItem>
) {
    data class Limit(
            val limit: Int,
            val today_viewed: Int
    )

    data class VideoItem(
            val id: Int,
            val title: String,
            val limit: Int,
            val award: Float,
            val image_url: String,
            val pivot: net.makemoney.android.data.responses.VideoResponse.Pivot?
    )

    data class Pivot(
            val views: Int,
            val today_views: Int,
            val earned: Float,
            val is_available: Boolean
    )
}