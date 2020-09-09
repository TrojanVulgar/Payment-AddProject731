package net.makemoney.android.data.models.details

import net.makemoney.android.R


data class DetailsSimpleItem(
        val title: String = "",
        val conditional: String = "",
        val type: Int = -1,
        val icon: Int = 0,
        val color: Int = 0,
        val shape: Int = R.drawable.shape_referral_icon
) : net.makemoney.android.data.models.details.DetailsItem()