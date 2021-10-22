package com.trickyandroid.playmusic.utils

import android.provider.MediaStore

object Constants {
    val IDLE: String = "PlayStatus_IDLE"
    val LOADING: String = "PlayStatus_LOADING"
    val PLAYING: String = "PlayStatus_PLAYING"
    val PAUSED: String = "PlayStatus_PAUSED"
    val FORWARD: String = "PlayStatus_FORWARD"
    val BACKWARD: String = "PlayStatus_BACKWARD"
    val STOPPED: String = "PlayStatus_STOPPED"
    val ERROR: String = "PlayStatus_ERROR"

    const val ACTION_PLAY = "com.trickyandroid.playmusic.ACTION_PLAY"
    const val ACTION_PAUSE = "com.trickyandroid.playmusic.ACTION_PAUSE"
    const val ACTION_STOP = "com.trickyandroid.playmusic.ACTION_STOP"
    const val ACTION_FORWARD = "com.trickyandroid.playmusic.ACTION_FORWARD"
    const val ACTION_BACKWARD = "com.trickyandroid.playmusic.ACTION_BACKWARD"

    const val MediaStore_DURATION = MediaStore.Audio.Media.DURATION
    const val MediaStore_ID = MediaStore.Audio.Media._ID
    const val MediaStore_ALBUM_ID = MediaStore.Audio.Media.ALBUM_ID
    const val MediaStore_DATA = MediaStore.Audio.Media.DATA
    const val MediaStore_ALBUM = MediaStore.Audio.Media.ALBUM
    const val MediaStore_ARTIST = MediaStore.Audio.Media.ARTIST
    const val MediaStore_ALBUM_MART = "content://media/external/audio/albumart"
    const val MediaStore_TITLE = MediaStore.Audio.Media.TITLE
    const val MediaStore_COMPOSER = MediaStore.Audio.Media.COMPOSER
    const val MediaStore_SIZE = MediaStore.Audio.Media.SIZE

}