package com.trickyandroid.playmusic.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.trickyandroid.playmusic.view.activitys.*
import com.trickyandroid.playmusic.view.fragement.AlbumsTab
import com.trickyandroid.playmusic.view.fragement.ArtistsTab
import com.trickyandroid.playmusic.view.fragement.TracksTab
import com.trickyandroid.playmusic.service.Mp3PlayerService
import com.trickyandroid.playmusic.service.Mp3Receiver
import com.trickyandroid.playmusic.utils.CustomProgressBar

class AppController : Application() {

    //Static  methods & variable
    companion object {
        val TAG = AppController::class.java.simpleName
        @SuppressLint("StaticFieldLeak")
        @get:Synchronized
        var mainActivity: MainActivity? = null
        var splashActivity: SplashActivity? = null
        var mp3PlayerService: Mp3PlayerService? = null
        var equalizerActivity: EqualizerActivity? = null
        var songPlayerActivity: SongPlayerActivity? = null
        var onlineRadioActivity: OnlineRadioActivity? = null
        var customProgressBar: CustomProgressBar? = null
        var mp3Receiver: Mp3Receiver? = null
        var albumsTab: AlbumsTab? = null
        var tracksTab: TracksTab? = null
        var artistsTab: ArtistsTab? = null
        var albumSongActivity: AlbumSongActivity? = null

        @SuppressLint("StaticFieldLeak")
        var instance: AppController? = null
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = this.applicationContext
    }
}