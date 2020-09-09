package net.makemoney.android.data.models.details

import net.makemoney.android.R
import net.makemoney.android.extensions.getString


data class ProgressItem(
        val title: String = getString(R.string.profile_progress_empty_title),
        val level: Int = 0,
        val currentProgress: Int = 0,
        val maxProgress: Int = 0,
        val percent: Int = 0,
        val icon: Int = R.drawable.ic_star,
        val color: Int = R.color.colorWhite,
        val levelShape: Int = R.drawable.shape_profile_level
) : net.makemoney.android.data.models.details.DetailsItem()