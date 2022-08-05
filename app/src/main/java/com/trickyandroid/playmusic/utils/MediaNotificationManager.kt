package com.trickyandroid.playmusic.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.service.Mp3PlayerService
import com.trickyandroid.playmusic.view.activitys.MainActivity
import com.trickyandroid.playmusic.view.activitys.OnlineRadioActivity
import okhttp3.internal.notify


class MediaNotificationManager constructor(val service: Mp3PlayerService) {

    private val NOTIFICATIONID = 555
    private val PRIMARYCHANNEL = "PRIMARY_CHANNEL_ID"
    private val PRIMARYCHANNELNAME = "PRIMARY"
    private var notificationManager: NotificationManagerCompat? = null
    private var resources: Resources? = null
    private var mediaSessionCompat:MediaSessionCompat?=null

    init {
        resources = service.resources
        notificationManager = NotificationManagerCompat.from(service)
    }

    fun startNotify(playbackStatus: String) {

        try {
            if(!service.path.isNullOrEmpty())
            Mp3PlayerService.retriever.setDataSource(service.path)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val art = Mp3PlayerService.retriever.embeddedPicture
        var bitmap: Bitmap? = null
        val songName = if (MainActivity.isFmPlay) MainActivity.SongsInfoList[MainActivity.currentSongIndex].getSongName() else Mp3PlayerService.retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val movieName = if (MainActivity.isFmPlay) MainActivity.SongsInfoList[MainActivity.currentSongIndex].getSongMoviename() else Mp3PlayerService.retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_ALBUM)
        if (art != null) {
            var options = BitmapFactory.Options()
            options.inPurgeable = true
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.size, options)
        }

        if (bitmap == null || MainActivity.isFmPlay) {
            bitmap = (resources?.getDrawable(R.drawable.default_album_bg) as BitmapDrawable).bitmap
        }
        var largeIcon: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_music)

        var playnPauseicon: Int = R.drawable.ic_pause_black_24dp
        val playbackAction = Intent(service, Mp3PlayerService::class.java)
        playbackAction.action = Constants.ACTION_PAUSE
        var action = PendingIntent.getService(service, 1, playbackAction, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)  PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)

        if (playbackStatus == Constants.PAUSED) {
            playnPauseicon = R.drawable.ic_play_arrow_white_24dp
            playbackAction.action = Constants.ACTION_PLAY
            action = PendingIntent.getService(service, 2, playbackAction, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val previousIntent = Intent(service, Mp3PlayerService::class.java)
        previousIntent.action = Constants.ACTION_BACKWARD
        val previousAction = PendingIntent.getService(service, 4, previousIntent,  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(service, Mp3PlayerService::class.java)
        nextIntent.action = Constants.ACTION_FORWARD
        val nextAction = PendingIntent.getService(service, 5, nextIntent,if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)   PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)

        val stopIntent = Intent(service, Mp3PlayerService::class.java)
        stopIntent.action = Constants.ACTION_STOP
        val stopAction: PendingIntent = PendingIntent.getService(service, 3, stopIntent,if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)  PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)

        val destActivity =
            if (MainActivity.isFmPlay) OnlineRadioActivity::class.java else MainActivity::class.java
        val i = Intent(service.appContext, destActivity)
        val taskStackBuilder: TaskStackBuilder = TaskStackBuilder.create(service.appContext)
        taskStackBuilder.addParentStack(MainActivity::class.java)
        taskStackBuilder.addNextIntent(i)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(service, 0, i,  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationView = RemoteViews(service.packageName, R.layout.notificationbig_mediacontroller)
        notificationManager!!.cancel(NOTIFICATIONID)
        mediaSessionCompat =  MediaSessionCompat(service, "tag")

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val manager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            mediaSessionCompat =  MediaSessionCompat(service, "tag")
            mediaSessionCompat?.isActive=true
//        mediaSessionCompat?.
//            /* Below coding for above android 12 notification specify
            mediaSessionCompat?.setMetadata(
                 MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE,songName)
                    .putString(MediaMetadata.METADATA_KEY_ARTIST,movieName)
                    .build()
            )
//            val channel = NotificationChannel(PRIMARYCHANNEL, PRIMARYCHANNELNAME, NotificationManager.IMPORTANCE_HIGH)
//            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////               channel.importance=
//            }
//
////            manager.createNotificationChannel(channel)
//        }

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(service, PRIMARYCHANNEL)
                .setAutoCancel(false)
                .setContentTitle(songName)
                .setContentText(movieName)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
//                .setContent(notificationView)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "previous", previousAction)
                .addAction(playnPauseicon, "pause", action)
                .addAction(R.drawable.ic_baseline_skip_next_24, "next", nextAction)
                .addAction(R.drawable.ic_close_black_24dp, "stop", stopAction)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat?.sessionToken)
                        .setShowActionsInCompactView(0, 1, 2,3)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction)

//                        .buildIntoRemoteViews(notificationView)
                )


        service.startForeground(NOTIFICATIONID, builder.build())
    }

    fun cancelNotify() {
        service.stopForeground(true)
    }
}