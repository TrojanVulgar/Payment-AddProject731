package net.makemoney.android.data.responses

/**
 * Created by Artem on 18.03.2019.
 * Contact the developer - Artem.Hunfrit@gmail.com
 * company - A2Lab
 */

data class GameResponseItem(
        val id: Int,
        val title: String,
        val image_url: String,
        val pivot: net.makemoney.android.data.responses.GameResponseItem.Pivot
) {
    data class Pivot(
        val is_available: Boolean
    )
}