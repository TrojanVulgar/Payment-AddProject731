package net.makemoney.android.data.responses


data class MarathonResponseItem(
    val id: Int,
    val title: String,
    val current: Int,
    val max: Int,
    val status: Int, // 0 - unavailable, 1 - available, 2 - done
    val award: List<Float>
)