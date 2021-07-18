package com.trickyandroid.playmusic.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.view.activitys.MainActivity
import com.trickyandroid.playmusic.view.activitys.OnlineRadioActivity
import com.trickyandroid.playmusic.service.Mp3PlayerService

class MediaNotificationManager constructor(val service: Mp3PlayerService) {

    val NOTIFICATION_ID = 555
    private val PRIMARY_CHANNEL = "PRIMARY_CHANNEL_ID"
    private val PRIMARY_CHANNEL_NAME = "PRIMARY"
    private var notificationManager: NotificationManagerCompat? = null
    private var resources: Resources? = null

    init {
        resources = service.getResources()
        notificationManager = NotificationManagerCompat.from(service)
    }


    fun startNotify(playbackStatus: String) {

        try {
            if(!service.path.isNullOrEmpty())
            Mp3PlayerService.retriever.setDataSource(service.path)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var art = Mp3PlayerService.retriever.getEmbeddedPicture()
        var bitmap: Bitmap? = null
        val songName = if(MainActivity.isFmPlay)MainActivity.SongsInfoList[MainActivity.currentSongIndex].getSongName()  else Mp3PlayerService.retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val movieName = if(MainActivity.isFmPlay)MainActivity.SongsInfoList[MainActivity.currentSongIndex].getSongMoviename() else Mp3PlayerService.retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        if (art != null) {
            var options = BitmapFactory.Options()
            options.inPurgeable = true
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.size, options)
        }

        if (bitmap == null|| MainActivity.isFmPlay) {
            bitmap = (resources?.getDrawable(R.drawable.default_album_bg) as BitmapDrawable).getBitmap()
        }
        var largeIcon: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_music)

        var playnPauseicon: Int = R.drawable.ic_pause_black_24dp
        val playbackAction = Intent(service, Mp3PlayerService::class.java)
        playbackAction.action = Constants.ACTION_PAUSE
        var action = PendingIntent.getService(service, 1, playbackAction, 0)

        if (playbackStatus.equals(Constants.PAUSED)) {
            playnPauseicon = R.drawable.ic_play_arrow_white_24dp
            playbackAction.action = Constants.ACTION_PLAY
            action = PendingIntent.getService(service, 2, playbackAction, 0)
        }

        val previousIntent = Intent(service, Mp3PlayerService::class.java)
        previousIntent.action = Constants.ACTION_BACKWARD
        val previousAction = PendingIntent.getService(service, 4, previousIntent, 0)

        val nextIntent = Intent(service, Mp3PlayerService::class.java)
        nextIntent.action = Constants.ACTION_FORWARD
        val nextAction = PendingIntent.getService(service, 5, nextIntent, 0)

        val stopIntent = Intent(service, Mp3PlayerService::class.java)
        stopIntent.action = Constants.ACTION_STOP
        val stopAction: PendingIntent = PendingIntent.getService(service, 3, stopIntent, 0)

        val destActivity= if (MainActivity.isFmPlay) OnlineRadioActivity::class.java else MainActivity::class.java
        val i = Intent(service.appContext, destActivity)
        var taskStackBuilder: TaskStackBuilder = TaskStackBuilder.create(service.appContext)
        taskStackBuilder.addParentStack(MainActivity::class.java)
        taskStackBuilder.addNextIntent(i)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(service, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)

        notificationManager!!.cancel(NOTIFICATION_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(PRIMARY_CHANNEL, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            manager.createNotificationChannel(channel)
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(service, PRIMARY_CHANNEL)
                .setAutoCancel(false)
                .setContentTitle(songName)
                .setContentText(movieName)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "previous", previousAction)
                .addAction(playnPauseicon, "pause", action)
                .addAction(R.drawable.ic_baseline_skip_next_24, "next", nextAction)
                .addAction(R.drawable.ic_close_black_24dp, "stop", stopAction)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setStyle( androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(service.getMediaSession()!!.getSessionToken())
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction))

        service.startForeground(NOTIFICATION_ID, builder.build())
    }

    fun cancelNotify() {
        service.stopForeground(true)
    }


}