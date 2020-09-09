package net.makemoney.android.data.models.country

import com.google.gson.annotations.SerializedName

data class CountryItem(
        val id: Int = 0,
        @SerializedName("country_name_ru") val nameRu: String = "",
        @SerializedName("country_name_en") val nameEn: String = ""
)