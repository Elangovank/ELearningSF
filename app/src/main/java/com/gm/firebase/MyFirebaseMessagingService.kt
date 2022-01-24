package com.gm.firebase

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gm.R
import com.gm.controllers.activities.HomeActivity
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var notification = Model.Notificaiton()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage == null)
            return
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
        }

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            try {
                var params = remoteMessage.data;
                var json = JSONObject(params as Map<*, *>)
                notification = Gson().fromJson(json.toString(), Model.Notificaiton::class.java)
                handleData(notification)
            } catch (e: EnumConstantNotPresentException) {
                e.printStackTrace()
            }

        }
    }

    private fun isAppOnForeground(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
    }

    private fun handleData(notification: Model.Notificaiton) {
        var intent = Intent(applicationContext, HomeActivity::class.java)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!isAppOnForeground()) {
            intent = Intent(applicationContext, HomeActivity::class.java)

        }
        intent.putExtra("notification", notification)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_ID = GMKeys.ChannelID// The id of the channel.
            val name = GMKeys.Notification// The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(mChannel)
            val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID
                    ?: "")
                    .setContentTitle(notification.title)
                    .setSmallIcon(R.drawable.ic_notification_coloured)
                    //.setBadgeIconType(R.drawable.ic_add_icon)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(Html.fromHtml(notification.message).toString())))
                    .setContentText(Html.fromHtml(Html.fromHtml(notification.message).toString()))
                    .setColor(getColor(R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true)
            notification.notificationTypeId?.let { notificationManager.notify(it, notificationBuilder.build()) }
        } else {
            val notificationBuilder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notification_coloured)
                    //  .setBadgeIconType(R.drawable.notification_icon_sales)
                    .setColor(resources.getColor(R.color.colorPrimary))
                    .setContentTitle(notification.title)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(Html.fromHtml(Html.fromHtml(notification.message).toString())))
                    .setContentText(Html.fromHtml(Html.fromHtml(notification.message).toString()))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
            notificationManager.notify(notification.notificationTypeId!!, notificationBuilder.build())
        }
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}