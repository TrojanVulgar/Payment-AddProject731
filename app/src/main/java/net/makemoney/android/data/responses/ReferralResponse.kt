package net.makemoney.android.data.responses

import com.google.gson.annotations.SerializedName


data class ReferralResponse(
        val referrals: net.makemoney.android.data.responses.ReferralResponse.ReferralProgressResponseItem,
        val balance: Float,
        val award: Int,
        val paid: Float
) {
    data class ReferralProgressResponseItem(
            val level: Int,
            @SerializedName("current")
            val currentProgress: Int,
            @SerializedName("max")
            val maxProgress: Int,
            val percent: Int
    )
}