package net.makemoney.android.api

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
        @SerializedName("error") val error: String
)
