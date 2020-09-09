package net.makemoney.android.mvp.contracts

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import net.makemoney.android.data.models.country.CountryItem
import net.makemoney.android.data.models.marathon.MarathonItem
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView

interface StartContract {

    interface View : BaseView {
        fun retrieveMarathon(marathonItem: net.makemoney.android.data.models.marathon.MarathonItem)

        fun onDataFetchError(error: String)

        fun onLoggedIn(isNew: Boolean)

        fun onPromoConfirmed()

        fun onCountriesFetched(countries: List<net.makemoney.android.data.models.country.CountryItem>)

        /**
         * When response is unsuccessful need to hide progress bar and show login button
         */
        fun updateUI()

    }

    interface Presenter : BasePresenter<View> {
        fun startFetchingData()

        fun signIn(result: Task<GoogleSignInAccount>)

        fun registerPromo(promo: String)

        fun registerUser(countryId: Int)
    }
}