package net.makemoney.android.firebase

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import net.makemoney.android.R
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.responses.EmptyResponse
import net.makemoney.android.extensions.appContext
import net.makemoney.android.extensions.getColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

/**
 * Created by hunfrit on 07.09.18.
 * Contact the developer - Artem.Hunfrit@gmail.com
 * company - A2Lab
 */

class FirebaseCloudMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val NOTIFICATION_CHANNEL_ID = getString(R.string.notification_id)
        remoteMessage?.let { message ->
            Timber.d("FCM, From: ${message.from}")
            if (message.notification != null) {
                Timber.d("FCM, Message Notification Body: ${message.notification?.body}")
                val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Notification.Builder(appContext, NOTIFICATION_CHANNEL_ID)
                } else {
                    @Suppress("DEPRECATION")
                    Notification.Builder(appContext)
                }
                val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
                }
                with(notification) {
                    setSmallIcon(R.drawable.logo)
                    setColor(getColor(source = R.color.colorPrimary))
                    setAutoCancel(true)
                    setContentTitle(message.notification?.title ?: getString(R.string.app_name))
                    setContentText(message.notification?.body ?: "")
                    setStyle(Notification.BigTextStyle().bigText(message.notification?.body ?: ""))
                }
                notificationManager.notify(Random().nextInt(), notification.build())
            }
        }
    }

    override fun onNewToken(newToken: String?) {
        Timber.d("FCM, New token: $newToken")
        newToken?.let {
            net.makemoney.android.api.RestClient.api.setFCMToken(it).enqueue(object : Callback<net.makemoney.android.data.responses.EmptyResponse> {
                override fun onResponse(call: Call<net.makemoney.android.data.responses.EmptyResponse>, response: Response<net.makemoney.android.data.responses.EmptyResponse>) {}

                override fun onFailure(call: Call<net.makemoney.android.data.responses.EmptyResponse>, t: Throwable) {
                    Timber.i("FCM, new token sending failure: ${t.message}")
                }
            })
        }
    }
}