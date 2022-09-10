package com.trickyandroid.playmusic.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trickyandroid.playmusic.service.Mp3PlayerService



class MainViewModel : ViewModel(), LifecycleObserver {

    companion object {
        val TAG = "MainactivityViewModel"
        private var instance: MainViewModel? = null
        fun getInstance() =
            instance ?: synchronized(MainViewModel::class.java) {
                instance ?: MainViewModel().also { instance = it }
            }
    }

    var _binder= MutableLiveData<Mp3PlayerService.LocalBinder>()
    var isSeekBarUpdating = MutableLiveData<Boolean>()
    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d(TAG, "OnServiceConnected:connected to service ")
            val binder: Mp3PlayerService.LocalBinder = p1 as Mp3PlayerService.LocalBinder
            _binder.postValue(binder)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            _binder.postValue(null!!)
        }
    }

    init {
    }



    fun getIsSeekBarUdpating(): LiveData<Boolean> = isSeekBarUpdating

    fun getBindingsUdpating(): LiveData<Mp3PlayerService.LocalBinder> = _binder!!

    fun setIsUpdateSeekbar(isUpdate: Boolean) = isSeekBarUpdating.postValue(isUpdate)

}