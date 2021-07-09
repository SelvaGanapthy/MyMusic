//package com.trickyandroid.playmusic.service
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.graphics.Color
//import android.os.Build
//import android.support.annotation.RequiresApi
//import android.support.v4.app.NotificationCompat
//import android.util.Log
//
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import com.trickyandroid.playmusic.R
//
//import java.util.Random
//
//import android.content.Context.NOTIFICATION_SERVICE
//import android.media.RingtoneManager
//import com.trickyandroid.playmusic.view.activitys.MainActivity
//import android.content.Context.NOTIFICATION_SERVICE
//import com.trickyandroid.playmusic.view.activitys.SplashActivity
//
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
////    internal var NOTIFICATION_CHANNEL_ID = "com.trickyandroid.playmusic"
////
////    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
////    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
////        super.onMessageReceived(remoteMessage)
////        //        if (remoteMessage.getData().isEmpty()) {
////        loadnotification(remoteMessage!!.notification!!.body, remoteMessage.notification!!.title)
////        //        } else {
////        //            loadnotification(remoteMessage.getData());
////        //        }
////    }
////
////
////    fun loadnotification(data: Map<String, String>) {
////        val pi = PendingIntent.getActivities(this, 0, arrayOf(Intent(this, MainActivity::class.java)), 0)
////        val title = data["title"].toString()
////        val body = data["body"].toString()
////        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT)
////            notificationChannel.description = "Test Channel"
////            notificationChannel.enableLights(true)
////            notificationChannel.lightColor = Color.BLUE
////            notificationChannel.vibrationPattern = longArrayOf(0, 100, 500, 1000)
////            notificationManager.createNotificationChannel(notificationChannel)
////        }
////
////        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
////        notificationBuilder.setAutoCancel(true)
////                .setDefaults(Notification.DEFAULT_ALL)
////                .setWhen(System.currentTimeMillis())
////                .setSmallIcon(R.drawable.ic_share_white_24dp)
////                .setContentTitle(title)
////                .setContentIntent(pi)
////                .setContentText(body)
////        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
////
////    }
////
////
////    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
////    fun loadnotification(contentText: String?, title: String?) {
////        val pi = PendingIntent.getActivities(this, 0, arrayOf(Intent(this, MainActivity::class.java)), 0)
////        val notification = Notification.Builder(this)
////                //                .setSmallIcon(R.drawable.ic_stat_name)
////                .setContentText(contentText)
////                .setContentTitle(title)
////                .setContentIntent(pi)
////                .setAutoCancel(false)
////                .build()
////        //
////        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
////        notificationManager.notify(0, notification)
////
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT)
////            notificationChannel.description = "Test Channel"
////            notificationChannel.enableLights(true)
////            notificationChannel.lightColor = Color.BLUE
////            notificationChannel.vibrationPattern = longArrayOf(0, 100, 500, 1000)
////            notificationManager.createNotificationChannel(notificationChannel)
////        }
////
////        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
////        notificationBuilder.setAutoCancel(true)
////                .setDefaults(Notification.DEFAULT_ALL)
////                .setWhen(System.currentTimeMillis())
////                //                .setSmallIcon(R.drawable.ic_stat_name)
////                .setContentTitle(title)
////                .setContentIntent(pi)
////                .setContentText(contentText)
////        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
////
////    }
////
////
////    override fun onNewToken(s: String?) {
////        super.onNewToken(s)
////        Log.i("TokenId ", s)
////    }
//
//
//    var NOTIFICATION_CHANNEL_ID = "com.trickyandroid.playmusic"
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
//        super.onMessageReceived(remoteMessage)
//        if (remoteMessage!!.data.size > 0) {
//            sendNotification(remoteMessage)
//        }
//    }
//
//
//    fun sendNotification(remoteMessage: RemoteMessage) {
//
//        val data = remoteMessage.data
//
//        val title = data["title"].toString()
//        val body  = data["body"].toString()
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val pi = PendingIntent.getActivities(this, 0, arrayOf(Intent(this, SplashActivity::class.java)), 0)
//
//        /*Set Defult Ringtone Sound for Push Notification*/
//        val defaultNotifySound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT)
//            notificationChannel.description = "Test Channel"
//            notificationChannel.enableLights(true)
//            notificationChannel.lightColor = Color.BLUE
//            notificationChannel.vibrationPattern = longArrayOf(0, 100, 500, 1000)
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//
//
//        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//        notificationBuilder.setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.mipmap.ic_launcher_music)
//                .setContentTitle(title)
//                .setContentIntent(pi)
//                .setContentText(body)
//        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
//
//
//    }
//
//
//}
