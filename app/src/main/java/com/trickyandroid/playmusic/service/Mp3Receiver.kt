package com.trickyandroid.playmusic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

class Mp3Receiver : BroadcastReceiver() {


    companion object {
        var lastState: Int = TelephonyManager.CALL_STATE_IDLE
        var callPause: Boolean = false
    }

    override fun onReceive(p0: Context?, intent: Intent?) {
        try {

//            AppController.mp3Receiver = this
//
//            if (intent!!.action.equals(Intent.ACTION_MEDIA_BUTTON)) {
//                val keyEvent = intent.extras!!.get(Intent.EXTRA_KEY_EVENT) as KeyEvent
//                if (keyEvent.action != KeyEvent.ACTION_DOWN)
//                    return
//                when (keyEvent.keyCode) {
//                    KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> if (MainActivity.exoPlayer != null && MainActivity.exoPlayer?.playWhenReady!!) {
//                        AppController.mainActivity?.SongPause()
//                    } else {
//                        AppController.mainActivity?.SongPlay()
//                    }
//
//                    KeyEvent.KEYCODE_MEDIA_PLAY -> Toast.makeText(AppController.context, "play", Toast.LENGTH_SHORT).show()
//
//                    KeyEvent.KEYCODE_MEDIA_PAUSE -> {
//                    }
//
//                    KeyEvent.KEYCODE_MEDIA_STOP -> {
//                    }
//
//                    KeyEvent.KEYCODE_MEDIA_NEXT -> Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT")
//                    KeyEvent.KEYCODE_MEDIA_PREVIOUS -> Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS")
//                }// Controls.nextControl(context)
//                // Controls.previousControl(context);
//            } else if (intent!!.action.equals("android.intent.action.NEW_OUTGOING_CALL")) {
//                if (MainActivity.mediaPlayer != null && MainActivity.mediaPlayer!!.isPlaying()) {
//                    AppController.mainActivity?.SongPause()
//                    OnlineRadioActivity.stopOnlineRadio()
//                    callPause = true
//                }
//            } else {
//                var stateStr = intent.extras.getString(TelephonyManager.EXTRA_STATE)
//                var number = intent.extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
//                var state = 0
//                if (stateStr == TelephonyManager.EXTRA_STATE_IDLE) {
//                    state = TelephonyManager.CALL_STATE_IDLE
//                    if (MainActivity?.mediaPlayer != null && callPause === true) {
//                        AppController.mainActivity?.SongPlay()
//                        callPause = false
//                    }
//                } else if (stateStr == TelephonyManager.EXTRA_STATE_OFFHOOK) {
//                    state = TelephonyManager.CALL_STATE_OFFHOOK
//                    if (MainActivity?.mediaPlayer != null && MainActivity?.mediaPlayer!!.isPlaying()) {
//                        callPause = true
//                        AppController.mainActivity?.SongPause()
//                    }
//
//                    if (OnlineRadioActivity.player != null && OnlineRadioActivity.player!!.isPlaying) {
//                        OnlineRadioActivity.stopOnlineRadio()
//                    }
//
//                } else if (stateStr == TelephonyManager.EXTRA_STATE_RINGING) {
//                    if (MainActivity?.mediaPlayer != null && MainActivity?.mediaPlayer!!.isPlaying()) {
//                        callPause = true
//                        AppController.mainActivity?.SongPause()
//                    }
//
//                    if (OnlineRadioActivity.player != null && OnlineRadioActivity.player!!.isPlaying) {
//                        OnlineRadioActivity.stopOnlineRadio()
//                    }
//
//                    state = TelephonyManager.CALL_STATE_RINGING
//                }
//            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}