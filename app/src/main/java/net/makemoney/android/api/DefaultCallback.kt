package net.makemoney.android.api

import android.text.TextUtils
import com.google.gson.Gson
import net.makemoney.android.R
import net.makemoney.android.extensions.getString
import net.makemoney.android.skeleton.presentation.BaseView
import net.makemoney.android.utils.InstantDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.EOFException
import java.io.Reader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.UnknownHostException

/**
 * Class designed to replace default retrofit2 Callback and reduce overridden methods.
 * This class also provides default error handling and displays error message using {@link AlertDialog}
 */
abstract class DefaultCallback<T>(view: BaseView) : Callback<T> {

    companion object {
        const val TOKEN_MISMATCH = 401
        const val HTTP_NOT_FOUND = 404
        const val HTTP_TASK_IS_NOT_AVAILABLE = 410
        const val HTTP_NOT_ENOUGH_MONEY = 412
        const val HTTP_CARD_WAS_USED = 415
        const val HTTP_INCORRECT_DATA = 417
        const val HTTP_WRONG_FIELD = 422
        const val HTTP_QUIZ_ALREADY_DONE = 435
        const val HTTP_GAME_ALLREADY_DONE = 437
        const val HTTP_BAN_SPAM = 445
        const val HTTP_BAN_PROFIT = 446
        const val HTTP_BAN_REFERRALS = 447
        const val HTTP_BAN_RULES = 448
        const val HTTP_MARATHON_ALREADY_DONE = 467
        const val HTTP_SCREEN_ALREADY_MODERATION = 476
    }

    private val viewRef: WeakReference<BaseView> = WeakReference(view)

    abstract fun onResponse(body: T)

    override fun onResponse(call: Call<T>?, response: Response<T>) {
        hideLoading()
        if (response.code() == net.makemoney.android.api.DefaultCallback.Companion.TOKEN_MISMATCH) {
            viewRef.get()?.reboot()
            return
        }
        if (response.isSuccessful) {
            if (response.body() == null) {
                throw NullPointerException("Cannot deserialize response")
            }
            response.body()?.let {
                onResponse(it)
            }
        } else {
            showDialog(getErrorMessage(response))
        }
    }

    override fun onFailure(call: Call<T>?, t: Throwable?) {
        hideLoading()
        //display an error to user
        val message: String = when (t) {
            is EOFException -> getString(R.string.all_connection_error)
            is UnknownHostException -> getString(R.string.all_connection_error)
            else -> t?.message ?: getString(R.string.all_internal_server_errors)
        }

        showDialog(message)
        //log output
        try {
            Timber.e(t, "onFailure: %s", call?.request()?.url().toString())
        } catch (e: Exception) {
            Timber.e(t, "onFailure: Cannot get original request")
        }
    }

    /**
     * @return already generated or newly generated message
     */
    private fun getErrorMessage(response: Response<T>): String = when (response.code()) {
        HttpURLConnection.HTTP_INTERNAL_ERROR -> getString(R.string.all_internal_server_errors)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_BAN_SPAM -> getString(R.string.ban_reason_spam)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_BAN_PROFIT -> getString(R.string.ban_reason_profit)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_BAN_REFERRALS -> getString(R.string.ban_reason_referrals)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_BAN_RULES -> getString(R.string.ban_reason_rules)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_NOT_FOUND -> getString(R.string.all_internal_server_errors)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_NOT_ENOUGH_MONEY -> getString(R.string.withdrawal_not_enough)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_TASK_IS_NOT_AVAILABLE -> getString(R.string.all_task_is_not_available)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_QUIZ_ALREADY_DONE -> getString(R.string.all_quiz_not_available)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_GAME_ALLREADY_DONE -> getString(R.string.games_limit_over)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_CARD_WAS_USED -> getString(R.string.withdrawal_used_card_not_found_transaction)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_INCORRECT_DATA -> getString(R.string.all_wrong_fields)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_WRONG_FIELD -> getString(R.string.all_wrong_fields)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_MARATHON_ALREADY_DONE -> getString(R.string.all_marathon_already_done)
        net.makemoney.android.api.DefaultCallback.Companion.HTTP_SCREEN_ALREADY_MODERATION -> getString(R.string.all_screen_already_on_moderation)
        else -> getBadResponseMessage(response)
    }

    /**
     * @return parsed error response or default rest client error message
     */
    private fun getBadResponseMessage(response: Response<T>)
            : String = retrieveMessage(response).apply {
        if (this.isNullOrEmpty()) {
            return getString(R.string.all_internal_server_errors)
        } else {
            return@apply
        }
    }

    private fun retrieveMessage(response: Response<T>): String {
        val reader = getErrorReader(response)
                ?: return getString(R.string.all_internal_server_errors)
        // Get error text
        castBodyToSingleError(reader)?.let {
            return it.error
        }
        return getString(R.string.all_internal_server_errors)
    }

    /**
     * Parse an error json into [ErrorResponse] model
     */
    private fun castBodyToSingleError(reader: Reader): net.makemoney.android.api.ErrorResponse? {
        try {
            return Gson().fromJson<net.makemoney.android.api.ErrorResponse>(reader, net.makemoney.android.api.ErrorResponse::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Cannot parse from json")
        }
        return null
    }

    private fun getErrorReader(response: Response<T>): Reader? = response.errorBody()?.charStream()

    private fun showDialog(message: String) {
        Timber.d("showFailureDialog -> message[%s]", message)
        if (viewRef.get() == null) {
            Timber.i("showFailureDialog: viewReference == NULL")
            return
        }
        if (TextUtils.isEmpty(message)) {
            Timber.i("showFailureDialog: message is EMPTY")
            return
        }
        try {
            InstantDialog(viewRef.get()!!.getContext()).show(message)
        } catch (e: Exception) {
            Timber.e(e, "showFailureDialog: cannot create dialog")
        }

    }

    private fun hideLoading() {
        viewRef.get()?.hideProgressView()
    }
}
