package net.makemoney.android.mvp.presenters

import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.models.profile.ProfileItem
import net.makemoney.android.data.responses.EmptyResponse
import net.makemoney.android.mvp.contracts.ProfileDetailsContract


class ProfileDetailsPresenter(override val view: ProfileDetailsContract.View) : ProfileDetailsContract.Presenter {

    override fun saveProfileDetails(profileItem: net.makemoney.android.data.models.profile.ProfileItem) = net.makemoney.android.api.RestClient.api.saveProfileDetails(profileItem).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.EmptyResponse>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.EmptyResponse) {
            view.profileDetailsWasSaved()
        }
    })

}