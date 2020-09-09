package net.makemoney.android.mvp.presenters

import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.mappers.ProfileProgressMapper
import net.makemoney.android.data.responses.ProfileResponse
import net.makemoney.android.mvp.contracts.ProfileContract

class ProfilePresenter(override val view: ProfileContract.View) : ProfileContract.Presenter {

    private val mapper = net.makemoney.android.data.mappers.ProfileProgressMapper()

    override fun getProfileData() = net.makemoney.android.api.RestClient.api.getProfileData().enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.ProfileResponse>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.ProfileResponse) {
            view.retrieveProfileData(body.profileItem, body.progress.profile, mapper.transformTo(body.progress))
        }
    })
}