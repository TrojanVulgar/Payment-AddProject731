@file:Suppress("unused")

package net.makemoney.android.extensions

import android.text.TextUtils
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch
import net.makemoney.android.R
import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.ErrorResponse
import net.makemoney.android.skeleton.presentation.BaseView
import net.makemoney.android.utils.InstantDialog
import retrofit2.Response
import timber.log.Timber
import java.io.Reader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection

//This extensions used only when Retrofit used CoroutineAdapter
/**
 * Executes a request and return a result or exception if it occurs
 */
suspend fun <T : Any> Deferred<Response<T>>.startWith(view: BaseView): T? {
    val viewRef: WeakReference<BaseView> = WeakReference(view)
    try {
        val response = await()
        if (response.isSuccessful) {
            if (response.body() == null) {
                throw NullPointerException("Cannot deserialize response")
            }
            return response.body()
        } else {
            showDialog(viewRef, getErrorMessage(response))
        }
    } catch (e: Exception) {
        //Other exception occurs
        showDialog(viewRef, e.message ?: getString(R.string.all_connection_error))
    }
    return null
}

/**
 * @return already generated or newly generated message
 */
private fun getErrorMessage(response: Response<*>): String = when (response.code()) {
    HttpURLConnection.HTTP_INTERNAL_ERROR -> getString(R.string.all_connection_error)
    net.makemoney.android.api.DefaultCallback.HTTP_NOT_FOUND -> getString(R.string.all_connection_error)
    else -> getBadResponseMessage(response)
}

/**
 * @return parsed error response or default rest client error message
 */
private fun getBadResponseMessage(response: Response<*>)
        : String = retrieveMessage(response).apply {
    if (this.isEmpty()) {
        getString(R.string.all_connection_error)
    } else {
        return@apply
    }
}

private fun retrieveMessage(response: Response<*>): String {
    val reader = getErrorReader(response)
            ?: return getString(R.string.all_connection_error)
    // Get error text
    castBodyToSingleError(reader)?.let {
        return it.error
    }
    return getString(R.string.all_connection_error)
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

private fun getErrorReader(response: Response<*>): Reader? = response.errorBody()?.charStream()

private fun showDialog(viewRef: WeakReference<BaseView>, message: String) = launch(UI) {
    Timber.d("showFailureDialog -> message[%s]", message)
    if (viewRef.get() == null) {
        Timber.i("showFailureDialog: viewReference == NULL")
        return@launch
    }
    if (TextUtils.isEmpty(message)) {
        Timber.i("showFailureDialog: message is EMPTY")
        return@launch
    }
    try {
        InstantDialog(viewRef.get()!!.getContext()).show(message)
    } catch (e: Exception) {
        Timber.e(e, "showFailureDialog: cannot create dialog")
    }
}
