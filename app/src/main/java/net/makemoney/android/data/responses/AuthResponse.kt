package net.makemoney.android.data.responses

import com.google.gson.annotations.SerializedName


data class AuthResponse(
        @SerializedName("token") val token: String,
        @SerializedName("first_promo_code") val promoFirst: String,
        @SerializedName("second_promo_code") val promoSecond: String
)