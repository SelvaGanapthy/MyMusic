package com.trickyandroid.playmusic.viewmodel

import android.view.View
import androidx.lifecycle.LifecycleObserver
import java.util.*


class MainViewModel : Observable(), LifecycleObserver {

    init {
    }

    fun onClick(v: View) {
        setChanged()
        notifyObservers(v)
    }
}