package net.makemoney.android.utils

import net.makemoney.android.R
import net.makemoney.android.extensions.getString
import android.content.Context
import android.content.res.Resources
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.view.View
import java.lang.ref.WeakReference

/**
 * This is a simple AlertDialog which in most cases should be used for displaying useful information to user.
 * Class designed to ease process of creating AlertDialog
 */
class InstantDialog(context: Context, view: View? = null) {

    private var builder: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null

    init {
        val weakContext = WeakReference(context)
        weakContext.get()?.let {
            builder = if (view == null) {
                AlertDialog.Builder(it).setTitle(R.string.app_name)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok
                        ) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            weakContext.clear()
                        }
            } else {
                AlertDialog.Builder(it)
                        .setCancelable(false)
                        .setView(view)
            }
        }
    }

    /**
     * Create and display alert dialog
     */
    fun show(message: String = ""): AlertDialog? {
        builder?.apply {
            if (!message.isEmpty()) this.setMessage(message)
            dialog = this.create()
            dialog?.show()
        }
        return dialog
    }

    /**
     * Create and display alert dialog
     * @param resourceId message
     * @throws Resources.NotFoundException if the resource can't be found
     */
    @Throws(Resources.NotFoundException::class)
    fun show(@StringRes resourceId: Int): AlertDialog? = show(getString(resourceId))

    fun dismiss() = dialog?.dismiss()

}
