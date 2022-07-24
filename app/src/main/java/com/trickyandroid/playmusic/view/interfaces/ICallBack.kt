package com.trickyandroid.playmusic.view.interfaces

import android.content.Context
import android.content.Intent

interface ICallBack {
    fun callTelephoneStateChanged(context: Context,state:Int)
    fun callBackOnReceive(context: Context,intent: Intent)
}