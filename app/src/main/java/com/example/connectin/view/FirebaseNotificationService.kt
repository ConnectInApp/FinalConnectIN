package com.example.connectin.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.example.connectin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.collections.HashMap

var CHANNEL_ID="chats"


class FirebaseNotificationService:FirebaseMessagingService(){
    lateinit var auth:FirebaseAuth


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        updateToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {

        super.onMessageReceived(p0)
        if (p0.data.isNotEmpty()) {

            val map: Map<String, String> = p0.data

            val title = map["title"]
            val message = map["message"]
            val hisId = map["hisid"]
            val hisImage = map["photo"]
            val chatId = map["chatId"]

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                createOreoNotification(title!!, message!!, hisId!!, hisImage!!, chatId!!)
            else createNormalNotification(title!!, message!!, hisId!!, hisImage!!, chatId!!)

        }

    }
    private fun updateToken(token: String) {


        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser.uid)
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = token
        databaseReference.updateChildren(map)

    }
    private fun createNormalNotification(
        title: String,
        message: String,
        hisId: String,
        hisImage: String,
        chatId: String
    ) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setSound(uri)

        val intent = Intent(this, ChatActivity::class.java)

        intent.putExtra("hisId", hisId)
        intent.putExtra("hisImage", hisImage)
        intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        title: String,
        message: String,
        hisId: String,
        hisImage: String,
        chatId: String
    ) {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, ChatActivity::class.java)

        intent.putExtra("hisId", hisId)
        intent.putExtra("hisImage", hisImage)
        intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(100, notification)
    }
}