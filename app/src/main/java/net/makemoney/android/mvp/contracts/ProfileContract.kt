package net.makemoney.android.mvp.contracts

import net.makemoney.android.data.models.profile.ProfileItem
import net.makemoney.android.data.models.details.ProgressItem
import net.makemoney.android.data.responses.ProfileResponse
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView


interface ProfileContract {

    interface View : BaseView {
        fun retrieveProfileData(profileItem: net.makemoney.android.data.models.profile.ProfileItem, profileProgress: net.makemoney.android.data.responses.ProfileResponse.ProfileProgressResponseItem, additionalProgressItems: List<net.makemoney.android.data.models.details.ProgressItem>)
    }

    interface Presenter : BasePresenter<View> {
        fun getProfileData()
    }

}