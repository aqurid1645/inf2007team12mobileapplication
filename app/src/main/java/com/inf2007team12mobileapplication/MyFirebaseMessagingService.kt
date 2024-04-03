package com.inf2007team12mobileapplication

import com.inf2007team12mobileapplication.MainActivity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.inf2007team12mobileapplication.R
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val mainHandler = Handler(Looper.getMainLooper())
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var notificationManager: NotificationManager? = null
    var notificationIntent: Intent? = null
    var pendingIntent: PendingIntent? = null
    override fun onNewToken(s: String) {
        sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.putString("firebase", s)
        editor.apply()
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val params = remoteMessage.data
        Log.d("params",""+params.toString())
        if (remoteMessage.notification != null) {
            notificationIntent = Intent(applicationContext, MainActivity::class.java)
            notificationIntent!!.putExtra("notification", "notification")

            notificationIntent!!.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            notificationIntent!!.addCategory(Intent.CATEGORY_LAUNCHER)
            notificationIntent!!.action = Intent.ACTION_MAIN
            startActivity(notificationIntent)
            pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    this, 0,
                    notificationIntent, PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    this, 0,
                    notificationIntent, PendingIntent.FLAG_ONE_SHOT
                )
            }
            showNotification(
                this@MyFirebaseMessagingService,
                remoteMessage.notification!!.title, remoteMessage.notification!!.body,
                "", pendingIntent
            )
        } else if (remoteMessage.data != null) {
            notificationIntent = Intent(applicationContext, MainActivity::class.java)
            notificationIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            notificationIntent!!.putExtra("title", remoteMessage.data["title"])
            notificationIntent!!.putExtra("notification", "notification")
            pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
            }
            showNotification(
                this@MyFirebaseMessagingService,
                remoteMessage.data["title"],
                remoteMessage.data["body"],
                remoteMessage.data["action"], pendingIntent
            )
        }
    }

    private fun showNotification(
        context: Context, title: String?, body: String?,
        action: String?, pendingIntent: PendingIntent?
    ) {
        notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random().nextInt()
        val channelId = "123"
        val channelName = "FCM"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
        val builder: NotificationCompat.Builder
        builder = NotificationCompat.Builder(context, channelId)
//            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setSmallIcon(R.drawable.ic_launcher_background)

            .setCategory(Notification.CATEGORY_EVENT)
            .setColor(ContextCompat.getColor(this, R.color.purple_200, ))
            .setContentTitle(title)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
        notificationManager!!.notify(notificationId, builder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMessaging"
    }
}