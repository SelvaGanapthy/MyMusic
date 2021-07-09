package com.trickyandroid.playmusic.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.trickyandroid.playmusic.R
import com.trickyandroid.playmusic.app.AppController

public class CustomProgressBar(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_progressbar)
        AppController.customProgressBar = this
        setCancelable(false)
//        set backgroung transparent
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}