package net.makemoney.android.data.responses

import com.google.gson.annotations.SerializedName
import net.makemoney.android.data.models.country.CountryItem

data class GetCountriesResponse(
        val token: String? = null,
        @SerializedName("promo_code_first") val promoCodePrimary: String = "",
        @SerializedName("promo_code_second") val promoCodeSecondary: String = "",
        val countries: List<net.makemoney.android.data.models.country.CountryItem> = listOf()
)