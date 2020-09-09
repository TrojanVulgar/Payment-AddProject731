package net.makemoney.android.mvp.contracts

import net.makemoney.android.data.models.profile.ProfileItem
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView


interface ProfileDetailsContract {

    interface View : BaseView {
        fun profileDetailsWasSaved()
    }

    interface Presenter : BasePresenter<View> {
        fun saveProfileDetails(profileItem: net.makemoney.android.data.models.profile.ProfileItem)
    }

}