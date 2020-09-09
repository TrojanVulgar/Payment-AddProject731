package net.makemoney.android

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import net.makemoney.android.extensions.getColor
import net.makemoney.android.utils.TimberCrashReportingTree
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        net.makemoney.android.App.Companion.instance = this
        createNotificationChannel()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Fabric.with(this, Crashlytics())
            Timber.plant(TimberCrashReportingTree())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(net.makemoney.android.R.string.notification_channel)
            val description = getString(net.makemoney.android.R.string.notification_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(getString(net.makemoney.android.R.string.notification_id), name, importance)
            with(notificationChannel) {
                setShowBadge(true)
                enableLights(true)
                lightColor = getColor(source = net.makemoney.android.R.color.colorPrimary)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                this.description = description
            }
            val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {

        @Volatile
        private var instance: net.makemoney.android.App? = null

        val context: Context
            get() = net.makemoney.android.App.Companion.instance!!.applicationContext
    }

}
