package net.makemoney.android.mvp.presenters

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import net.makemoney.android.R
import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.mappers.MarathonMapper
import net.makemoney.android.data.responses.AuthResponse
import net.makemoney.android.data.responses.EmptyResponse
import net.makemoney.android.data.responses.GetCountriesResponse
import net.makemoney.android.data.responses.MarathonResponseItem
import net.makemoney.android.extensions.appContext
import net.makemoney.android.mvp.contracts.StartContract
import net.makemoney.android.utils.AppPreferences
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber

class StartPresenter(override val view: StartContract.View) : StartContract.Presenter {

    private val mapper = net.makemoney.android.data.mappers.MarathonMapper()
    private var userName = ""
    private var userEmail = ""

    override fun startFetchingData() {
        getMarathon()
    }

    override fun signIn(result: Task<GoogleSignInAccount>) {
        try {
            val account = result.getResult(ApiException::class.java)
            userName = account?.givenName ?: ""
            userEmail = account?.email ?: ""
            net.makemoney.android.api.RestClient.api.getCountries(userEmail)
                    .enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.GetCountriesResponse>(view) {
                        override fun onResponse(body: net.makemoney.android.data.responses.GetCountriesResponse) {
                            with(body) {
                                if (token == null) { //new user
                                    view.onCountriesFetched(countries)
                                } else {
                                    saveToPrefs(token, promoCodePrimary, promoCodeSecondary)
                                    view.onLoggedIn(false)
                                }
                            }
                        }
                    })
            Timber.i("SignInSuccess. Name: %s, Email: %s", userName, userEmail)
        } catch (e: ApiException) {
            Timber.e("SignInFailed. Status code: %d", e.statusCode)
            appContext.toast(getFailureCause(e.statusCode))
        }
    }

    override fun registerUser(countryId: Int) = net.makemoney.android.api.RestClient.api.auth(userName, userEmail, countryId)
            .enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.AuthResponse>(view) {
                override fun onResponse(body: net.makemoney.android.data.responses.AuthResponse) {
                    with(body) {
                        saveToPrefs(token, promoFirst, promoSecond)
                        view.onLoggedIn(true)
                    }
                }
            })

    override fun registerPromo(promo: String) = net.makemoney.android.api.RestClient.api.registerPromo(promo)
            .enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.EmptyResponse>(view) {
                override fun onResponse(body: net.makemoney.android.data.responses.EmptyResponse) {
                    view.onPromoConfirmed()
                }
            })

    private fun getMarathon() = net.makemoney.android.api.RestClient.api.getMarathon().enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.MarathonResponseItem>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.MarathonResponseItem) {
            view.retrieveMarathon(mapper.transformTo(body))
        }

        override fun onResponse(call: Call<net.makemoney.android.data.responses.MarathonResponseItem>?, response: Response<net.makemoney.android.data.responses.MarathonResponseItem>) {
            super.onResponse(call, response)
            if (!response.isSuccessful) view.updateUI()
        }

        override fun onFailure(call: Call<net.makemoney.android.data.responses.MarathonResponseItem>?, t: Throwable?) {
            super.onFailure(call, t)
            view.updateUI()
        }
    })

    private fun getFailureCause(statusCode: Int): Int = when (statusCode) {
        GoogleSignInStatusCodes.NETWORK_ERROR -> R.string.all_connection_error
        GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> R.string.all_smthing_cancel
        GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> R.string.all_already_pending_operation
        GoogleSignInStatusCodes.SIGN_IN_FAILED -> R.string.signin_auth_error
        else -> R.string.all_developer_errors//Check firebase console configuration
    }


    private fun saveToPrefs(token: String, primaryPromo: String, secondaryPromo: String) {
        AppPreferences.token = token
        AppPreferences.primaryPromo = primaryPromo
        AppPreferences.secondaryPromo = secondaryPromo
    }
}