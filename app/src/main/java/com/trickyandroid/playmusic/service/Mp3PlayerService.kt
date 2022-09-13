package com.trickyandroid.playmusic.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.app.AppController
import com.trickyandroid.playmusic.utils.Constants
import com.trickyandroid.playmusic.utils.MediaNotificationManager
import com.trickyandroid.playmusic.view.activitys.MainActivity
import com.trickyandroid.playmusic.view.activitys.OnlineRadioActivity
import com.trickyandroid.playmusic.view.activitys.SongPlayerActivity
import com.trickyandroid.playmusic.view.interfaces.ICallBack
import org.greenrobot.eventbus.EventBus

class Mp3PlayerService : Service(), AudioManager.OnAudioFocusChangeListener, Player.Listener,
    ICallBack {

    private var notificationId: Int = 12893565
    internal var path: String = ""
    private var notificationMgr: NotificationManager? = null
    private var mAudioManager: AudioManager? = null
    private var onGoingCall = false
    private var telephonyManager: TelephonyManager? = null
    internal var appContext: Context? = null
    private var status: String? = null
    private var _iCallBack: ICallBack? = null
    var progress: Int? = null
    var mHandler: Handler? = null

    //    Exo
    private var wifiLock: WifiManager.WifiLock? = null
    private val iBinder: IBinder = LocalBinder()
    private var audioManager: AudioManager? = null
    private var notificationManagers: MediaNotificationManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private val BANDWIDTHMETER: DefaultBandwidthMeter = DefaultBandwidthMeter()
    private var exoPlayer: ExoPlayer? = null

    inner class LocalBinder : Binder() {
        fun getService(): Mp3PlayerService = this@Mp3PlayerService
    }

    companion object {
        var retriever: MediaMetadataRetriever = MediaMetadataRetriever()
        var currentSongIndex: Int = 0
    }

    @RequiresApi(Build.VERSION_CODES.S)
    inner class phoneStateCallBackListener : TelephonyCallback(),
        TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            (_iCallBack as Mp3PlayerService).callTelephoneStateChanged(this@Mp3PlayerService, state)
        }
    }

    /*Below SDK S for Checking PhoneState*/
    private val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        @Deprecated("Deprecated in Java")
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            (_iCallBack as Mp3PlayerService).callTelephoneStateChanged(
                this@Mp3PlayerService,
                state
            )
        }
    }

    /*BroadCastReceiver*/
    private val becomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            (_iCallBack as Mp3PlayerService).callBackOnReceive(context, intent)
        }
    }

    inner class AudioPlayerBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            (_iCallBack as Mp3PlayerService).callBackOnReceive(context, intent)
        }
    }

    private val mediasSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            override fun onPause() {
                super.onPause()
                playOrPause()
            }

            override fun onStop() {
                super.onStop()
                stop()
            }

            override fun onPlay() {
                super.onPlay()
                playOrPause()
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                songForward()
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                songBackward()
            }

        }

    override fun onBind(p0: Intent?): IBinder = iBinder

/*open fun getMediaSession(): MediaSessionCompat? {
        return mediaSession
    }

    inner class ServiceThread() : Thread() {
        override fun run(): Unit {}
    }*/


    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()
        mHandler = Handler()
        onGoingCall = false
        _iCallBack = this@Mp3PlayerService
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        notificationManagers = MediaNotificationManager(this)
        appContext = AppController.context
        mediaSession = MediaSessionCompat(this, javaClass.simpleName)
        transportControls = mediaSession?.controller!!.transportControls
        mediaSession?.isActive = true
        mediaSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession?.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "...")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "selva")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "ganapathy")
                .build()
        )
        mediaSession?.setCallback(mediasSessionCallback)
        wifiLock = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
            .createWifiLock(WifiManager.WIFI_MODE_FULL, "mcScPAmpLock")
//        val trackSelectionFactory: AdaptiveTrackSelection.Factory = AdaptiveTrackSelection.Factory(BANDWIDTH_METER)
//        val trackSelector: DefaultTrackSelector = DefaultTrackSelector(trackSelectionFactory)
        exoPlayer = ExoPlayer.Builder(this).build()
        // ExoPlayer.newSimpleInstance(applicationContext, trackSelector)
        AppController.mp3PlayerService = this

        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            telephonyManager?.registerTelephonyCallback({ it.run() }, phoneStateCallBackListener())
        else
            telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)

        val filter = IntentFilter()
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        filter.addAction(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT.toString())
        registerReceiver(becomingNoisyReceiver, filter)
        MainActivity.exoPlayer?.addListener(this)
        status = Constants.IDLE
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            this.notificationMgr = getSystemService("notification") as NotificationManager
            this.exoPlayer = MainActivity.exoPlayer
            this.path =
                if (MainActivity.isFmPlay) "" else MainActivity.SongsInfoList[MainActivity.currentSongIndex]
                    .getSongPath()
            if (exoPlayer?.playWhenReady!!)
                notificationManagers?.startNotify(Constants.PLAYING)
            else
                notificationManagers?.startNotify(Constants.PAUSED)
            mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            mAudioManager?.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )

            EventBus.getDefault().post("selva")

            var action: String? = null
            action = if (intent?.action != null) intent.action else ""
            if (TextUtils.isEmpty(action) || action.equals("")) return START_NOT_STICKY

            if (this@Mp3PlayerService.exoPlayer != null) {
                when (action) {
                    Constants.ACTION_PLAY -> transportControls?.play()
                    Constants.ACTION_PAUSE -> transportControls?.pause()
                    Constants.ACTION_FORWARD -> transportControls?.skipToNext()
                    Constants.ACTION_BACKWARD -> transportControls?.skipToPrevious()
                    Constants.ACTION_STOP -> transportControls?.stop()
                }
            }

            if (MainActivity.isFmPlay && wifiLock != null && !wifiLock!!.isHeld) {
                wifiLock!!.acquire()
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return START_NOT_STICKY
    }

    fun playOrPause() {
        try {

            if (this@Mp3PlayerService.exoPlayer == null) {
                return
            }

            if (this@Mp3PlayerService.exoPlayer?.playWhenReady!!) {
                this@Mp3PlayerService.exoPlayer?.playWhenReady = false

                MainActivity.activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_play_arrow_black_24dp)

                if (SongPlayerActivity.activitySongPlayerBinding != null && SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause != null && SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                    SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()

                if (MainActivity.isFmPlay) {
                    OnlineRadioActivity.imvPlayrPause?.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                    wifiLockRelease()
                }

                notificationManagers?.startNotify(Constants.PAUSED)
                return
            }

            this@Mp3PlayerService.exoPlayer?.playWhenReady = true
            MainActivity.activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_pause_black_24dp)

            try {
                if (SongPlayerActivity.activitySongPlayerBinding != null && !SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                    SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (MainActivity.isFmPlay) {
                OnlineRadioActivity.imvPlayrPause?.setImageResource(R.drawable.ic_pause_black_24dp)
            }
            notificationManagers?.startNotify(Constants.PLAYING)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun songBackward() {
        if (this@Mp3PlayerService.exoPlayer != null) {
            currentSongIndex = MainActivity.currentSongIndex
            val size = MainActivity.SongsInfoList.size
            if (currentSongIndex > 0) {
                currentSongIndex--
                this@Mp3PlayerService.playSong(currentSongIndex)
                MainActivity.currentSongIndex = currentSongIndex
                if (status != Constants.IDLE)
                    notificationManagers?.startNotify(status!!)
                return
            }
            currentSongIndex = size - 1
            this@Mp3PlayerService.playSong(currentSongIndex)
            MainActivity.currentSongIndex = currentSongIndex
            if (status != Constants.IDLE)
                notificationManagers?.startNotify(status!!)
        }
        return
    }

    fun songForward() {
        if (this@Mp3PlayerService.exoPlayer != null) {
            currentSongIndex = MainActivity.currentSongIndex
            if (currentSongIndex < MainActivity.SongsInfoList.size - 1) {
                currentSongIndex++
                this@Mp3PlayerService.playSong(currentSongIndex)
                MainActivity.currentSongIndex = currentSongIndex
                if (status != Constants.IDLE)
                    notificationManagers?.startNotify(status!!)
                return
            }

            this@Mp3PlayerService.playSong(0)
            MainActivity.currentSongIndex = 0
            if (status != Constants.IDLE)
                notificationManagers?.startNotify(status!!)
        }
        return
    }

    fun stop() {
        MainActivity.exoPlayer?.playWhenReady = false
        exoPlayer!!.stop()
        audioManager!!.abandonAudioFocus(this)
        notificationManagers?.cancelNotify()
        if (MainActivity.isFmPlay)
            wifiLockRelease()
        try {
            if (MainActivity.isFmPlay)
                AppController.onlineRadioActivity?.layoutSongplay?.visibility = View.GONE

            if (AppController.songPlayerActivity != null)
                AppController.songPlayerActivity?.onBackPressed()
        } catch (e: java.lang.Exception) {

        }
//        AppController.mainActivity?.layoutSongplay?.visibility = View.GONE
        MainActivity.activityMainBinding.layoutSongplay.visibility = View.GONE
        AppController.albumSongActivity?.layoutSongplay?.visibility = View.GONE

    }

    override fun onDestroy() {

        this.notificationMgr?.cancel(this.notificationId)

        if (this.exoPlayer != null) {
            this.exoPlayer?.stop()
            this.exoPlayer?.release()
        }

        notificationManagers?.cancelNotify()

        mediaSession!!.release()

        if (telephonyManager != null) {
            telephonyManager?.listen(
                phoneStateListener as PhoneStateListener?,
                PhoneStateListener.LISTEN_NONE
            )
        }
        unregisterReceiver(becomingNoisyReceiver)
        super.onDestroy()
    }


    @SuppressLint("NewApi")
    fun playSong(songIndex: Int) {

        try {
            startExoplayer(MainActivity.SongsInfoList[songIndex].getSongPath())

            if (MainActivity.isFmPlay) {
                AppController.onlineRadioActivity?.setRadioDetails(MainActivity.SongsInfoList[songIndex])
//                MainActivity.SongsInfoList[MainActivity.currentSongIndex].setSongMoviename(MainActivity.SongsInfoList[songIndex].getSongMoviename())
//                MainActivity.SongsInfoList[MainActivity.currentSongIndex].setSongName(MainActivity.SongsInfoList[songIndex].getSongName())
                return
            }

            MainActivity.activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_pause_black_24dp)
            MainActivity.activityMainBinding.tvSongName.text=
                MainActivity.SongsInfoList[songIndex].getSongName() + " Song ."

            MainActivity.activityMainBinding.tvMovieName.text= MainActivity.SongsInfoList[songIndex].getSongMoviename()

            AppController.mainActivity?.initTotalsongTime()
            MainActivity.songTotalDurationLabel?.text= MainActivity.SongsInfoList[songIndex].getSongTime()

            AppController.songPlayerActivity?.initTotalTime()
            MainActivity.songTotalDurationLabel?.text
                MainActivity.SongsInfoList[songIndex].getSongTime()

            MainActivity.activityMainBinding.imvSongImage.setImageURI(Uri.parse(MainActivity.SongsInfoList[songIndex].getSongImgPath()))
//          MainActivity.imvSongImage?.setImageURI(Uri.parse(MainActivity.SongsInfoList.get(songIndex).getSongImgPath()))
//            MainActivity.activityMainBinding.songSeekBar.progress=0
//            MainActivity.activityMainBinding.songSeekBar.max=100
//            MainActivity.updateProgressBar()
            this.path = MainActivity.SongsInfoList[songIndex].getSongPath()
            SongPlayerActivity.songDetails(songIndex)
            notificationManagers?.startNotify(Constants.PLAYING)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: Exception) {
            this.exoPlayer?.stop()
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {

        if (this@Mp3PlayerService.exoPlayer == null) {
            return
        }

        when (focusChange) {

            AudioManager.AUDIOFOCUS_LOSS -> {

                if (this@Mp3PlayerService.exoPlayer?.playWhenReady!!) {
                    this@Mp3PlayerService.exoPlayer?.playWhenReady = false
                    if (SongPlayerActivity.activitySongPlayerBinding != null && SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                        SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()
                    MainActivity.activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                    notificationManagers?.startNotify(Constants.PAUSED)
                    return
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> if (this@Mp3PlayerService.exoPlayer?.playWhenReady!!) exoPlayer!!.volume =
                0.1f

            else -> return
        }
    }

    private fun getUserAgent(): String {
        return Util.getUserAgent(this, javaClass.simpleName)
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {

    }

    override fun onSeekProcessed() {
    }

    override fun onTracksChanged(tracks: Tracks) {

    }

    override fun onPlayerError(error: PlaybackException) {
        EventBus.getDefault().post(Constants.ERROR)
    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (!playWhenReady) {
            notificationManagers?.startNotify(Constants.PAUSED)
            status = Constants.PAUSED
        } else {
            notificationManagers?.startNotify(Constants.PLAYING)
            status = Constants.PLAYING
        }

        if (status != Constants.IDLE)
            notificationManagers?.startNotify(status!!)

        EventBus.getDefault().post(status)
    }

    private fun wifiLockRelease() {
        if (wifiLock != null && wifiLock!!.isHeld) {
            wifiLock!!.release()
        }
    }

    private fun startExoplayer(songpath: String) {
        this.exoPlayer!!.stop()
        val dataSourceFactory = DefaultDataSourceFactory(this, getUserAgent(), BANDWIDTHMETER)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(songpath)))
        this.exoPlayer!!.prepare(mediaSource)
        this.exoPlayer!!.playWhenReady = true
    }

    override fun callTelephoneStateChanged(context: Context, state: Int) {
        if (state == TelephonyManager.CALL_STATE_OFFHOOK
            || state == TelephonyManager.CALL_STATE_RINGING
        ) {
            if (!this@Mp3PlayerService.exoPlayer?.playWhenReady!!) return
            onGoingCall = true
            this@Mp3PlayerService.exoPlayer?.playWhenReady = false
            notificationManagers?.startNotify(Constants.PAUSED)
            MainActivity.activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            if (SongPlayerActivity.activitySongPlayerBinding != null && SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle() != null && SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()
        } else if (state == TelephonyManager.CALL_STATE_IDLE) {
            if (!onGoingCall) return
            onGoingCall = false

            if (this@Mp3PlayerService.exoPlayer != null) {
                this@Mp3PlayerService.exoPlayer?.playWhenReady = true
                notificationManagers?.startNotify(Constants.PLAYING)
                MainActivity.activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_pause_black_24dp)
                if (SongPlayerActivity.activitySongPlayerBinding != null && SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle() != null && !SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                    SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()
            }
        }
    }

    override fun callBackOnReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                if (exoPlayer != null && exoPlayer?.playWhenReady!!)
                    exoPlayer?.playWhenReady = false
                notificationManagers?.startNotify(Constants.PAUSED)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT.toString() -> {
                if (exoPlayer != null && !exoPlayer?.playWhenReady!!)
                    exoPlayer?.playWhenReady = true
                notificationManagers?.startNotify(Constants.PLAYING)
            }

            "ACTION_PLAY" -> try {

                if (this@Mp3PlayerService.exoPlayer == null) {
                    return
                }

                if (this@Mp3PlayerService.exoPlayer?.playWhenReady!!) {
                    this@Mp3PlayerService.exoPlayer?.playWhenReady = false

                    MainActivity.activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                    if (SongPlayerActivity.activitySongPlayerBinding != null && SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                        SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()

                    notificationManagers?.startNotify(Constants.PAUSED)
                    return
                }

                this@Mp3PlayerService.exoPlayer?.playWhenReady = true

                MainActivity.activityMainBinding.imvPlayrPause.setImageResource(R.drawable.ic_pause_black_24dp)

                try {
                    if (SongPlayerActivity.activitySongPlayerBinding != null && !SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle())
                        SongPlayerActivity.activitySongPlayerBinding!!.fabPlaynPause.toggle()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                notificationManagers?.startNotify(Constants.PLAYING)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            "ACTION_NEXT" -> {
                if (this@Mp3PlayerService.exoPlayer != null) {
                    currentSongIndex = MainActivity.currentSongIndex
                    if (currentSongIndex < MainActivity.SongsInfoList.size - 1) {
                        currentSongIndex++
                        this@Mp3PlayerService.playSong(currentSongIndex)
                        MainActivity.currentSongIndex = currentSongIndex
                        return
                    }

                    this@Mp3PlayerService.playSong(0)
                    MainActivity.currentSongIndex = 0
                }
            }

            "ACTION_PREVIOUS" -> if (this@Mp3PlayerService.exoPlayer != null) {
                currentSongIndex = MainActivity.currentSongIndex
                val size = MainActivity.SongsInfoList.size
                if (currentSongIndex > 0) {
                    currentSongIndex--
                    this@Mp3PlayerService.playSong(currentSongIndex)
                    MainActivity.currentSongIndex = currentSongIndex
                    return
                }
                currentSongIndex = size - 1
                this@Mp3PlayerService.playSong(currentSongIndex)
                MainActivity.currentSongIndex = currentSongIndex
            }
        }
    }

    fun startPretendLongRunningTask() {
        val mUpdateTimeTask: Runnable = object : Runnable {
            override fun run() {
                try {
                    val totalDuration = exoPlayer?.duration!!.toLong()
                    val currentDuration = exoPlayer!!.currentPosition

                    // Updating progress bar
                    progress = MainActivity.utils?.getProgressPercentage(currentDuration, totalDuration)

                    //Log.d("Progress", ""+progress);
                    //    MainActivity.activityMainBinding.songSeekBar.progress = progress!!
                    Log.i("@@", progress.toString())
                    // Running this thread after 100 milliseconds
                    mHandler!!.postDelayed(this, 100)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        mHandler!!.postDelayed(mUpdateTimeTask, 100)
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    fun getProgress(): Int = progress!!
    fun getExoplayer():ExoPlayer=exoPlayer!!
    fun setProgress(value: Long) {
        val totalDuration = exoPlayer?.duration!!.toLong()
        val currentDuration = value
        exoPlayer!!.seekTo(currentDuration)
        // Updating progress bar
        progress = MainActivity.utils?.getProgressPercentage(currentDuration, totalDuration)
    }
}