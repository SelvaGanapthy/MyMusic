package com.trickyandroid.playmusic.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SeekBar

@SuppressLint("AppCompatCustomView")
class VerticalSeekBar : SeekBar {

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

   internal fun mannualChange() {
        super.onSizeChanged(height, width, 0, 0)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(c: Canvas) {
        c.rotate(-90.0f)
        c.translate((-height).toFloat(), 0.0f)
        super.onDraw(c)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.action) {
            0, 1, 2 -> {
                progress = max - (max.toFloat() * event.y / height.toFloat()).toInt()
                onSizeChanged(width, height, 0, 0)
            }
        }
        return true
    }
}