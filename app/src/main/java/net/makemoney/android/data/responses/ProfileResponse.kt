package net.makemoney.android.data.responses

import com.google.gson.annotations.SerializedName
import net.makemoney.android.data.models.profile.ProfileItem


data class ProfileResponse(
        @SerializedName("profile")
        val profileItem: net.makemoney.android.data.models.profile.ProfileItem,
        @SerializedName("progress")
        val progress: net.makemoney.android.data.responses.ProfileResponse.ProfileProgressResponse
) {
    data class ProfileProgressResponse(
            val profile: net.makemoney.android.data.responses.ProfileResponse.ProfileProgressResponseItem,
            val tasks: net.makemoney.android.data.responses.ProfileResponse.ProfileProgressResponseItem,
            val videos: net.makemoney.android.data.responses.ProfileResponse.ProfileProgressResponseItem,
            val partners: net.makemoney.android.data.responses.ProfileResponse.ProfileProgressResponseItem,
            val referrals: net.makemoney.android.data.responses.ProfileResponse.ProfileProgressResponseItem
    )

    data class ProfileProgressResponseItem(
            val level: Int,
            @SerializedName("current")
            val currentProgress: Int,
            @SerializedName("max")
            val maxProgress: Int,
            val percent: Int
    )
}

