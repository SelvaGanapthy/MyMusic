package com.trickyandroid.playmusic.view.interfaces

import android.view.Menu

interface IFragmentListener {
    fun addiSearch(iSearch: ISearch): Unit
    fun removeISearch(iSearch: ISearch): Unit
    fun onCreateOptionsMenu(menu: Menu): Boolean
}