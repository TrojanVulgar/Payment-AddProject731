package net.makemoney.android.data.mappers

import net.makemoney.android.R
import net.makemoney.android.data.models.details.ProgressItem
import net.makemoney.android.data.responses.ProfileResponse
import net.makemoney.android.extensions.getString


class ProfileProgressMapper {

    fun transformTo(info: net.makemoney.android.data.responses.ProfileResponse.ProfileProgressResponse): List<net.makemoney.android.data.models.details.ProgressItem> {
        val tempList = arrayListOf<net.makemoney.android.data.models.details.ProgressItem>()
        info.apply {
            tempList.add(net.makemoney.android.data.models.details.ProgressItem(getString(R.string.profile_progress_tasks_title), tasks.level, tasks.currentProgress, tasks.maxProgress, tasks.percent, R.drawable.ic_profile_info_tasks, R.color.colorBalanceIcon, R.drawable.shape_profile_level))
            tempList.add(net.makemoney.android.data.models.details.ProgressItem(getString(R.string.profile_progress_videos_title), videos.level, videos.currentProgress, videos.maxProgress, videos.percent, R.drawable.ic_navigation_videos, R.color.colorBalanceIcon, R.drawable.shape_profile_level))
            tempList.add(net.makemoney.android.data.models.details.ProgressItem(getString(R.string.profile_progress_partners_title), partners.level, partners.currentProgress, partners.maxProgress, partners.percent, R.drawable.ic_profile_info_partners, R.color.colorBalanceIcon, R.drawable.shape_profile_level))
            tempList.add(net.makemoney.android.data.models.details.ProgressItem(getString(R.string.profile_progress_referrals_title), referrals.level, referrals.currentProgress, referrals.maxProgress, referrals.percent, R.drawable.ic_profile_info_referral, R.color.colorBalanceIcon, R.drawable.shape_profile_level))
        }
        return tempList
    }
}