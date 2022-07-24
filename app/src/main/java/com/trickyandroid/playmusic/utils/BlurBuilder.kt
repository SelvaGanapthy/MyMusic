package com.trickyandroid.playmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.RequiresApi


class BlurBuilder {

    private val BITMAPSCALE = 0.4f
    private val BLURRADIUS = 7.5f

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun blur(context: Context, image: Bitmap): Bitmap {
        val width = Math.round(image.width * BITMAPSCALE)
        val height = Math.round(image.height * BITMAPSCALE)

        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val rs = RenderScript.create(context)
        val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(BLURRADIUS)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)

        return outputBitmap
    }
}