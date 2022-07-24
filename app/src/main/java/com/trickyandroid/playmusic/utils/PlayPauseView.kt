package com.trickyandroid.playmusic.utils


import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.trickyandroid.playmusic.R


class PlayPauseView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var mAnimatorSet: AnimatorSet? = null
    private var mBackgroundColor = 0
    private var mDrawable: PlayPauseDrawable? = null
    private var mHeight = 0
    private val mPaint: Paint = Paint()
    private var mPauseBackgroundColor = 0
    private var mPlayBackgroundColor = 0
    private var mWidth = 0

    companion object {
        private val PLAYPAUSEANIMATIONDURATION: Long = 200

        private val COLOR: Property<PlayPauseView, Int> = object : Property<PlayPauseView, Int>(Int::class.java, "color") {
            override fun get(v: PlayPauseView): Int {
                return v.getColor()
            }

            override fun set(v: PlayPauseView, value: Int) {
                v.setColor(value)
            }
        }
    }

    init {
        setWillNotDraw(false)
        mBackgroundColor = resources.getColor(R.color.purple)
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mDrawable = PlayPauseDrawable(context)
        mDrawable?.callback=this
        mPauseBackgroundColor = resources.getColor(R.color.purple)
        mPlayBackgroundColor = resources.getColor(R.color.blue)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDrawable!!.setBounds(0, 0, w, h)
        mWidth = w
        mHeight = h
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = object : ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View, outline: Outline) {
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
            clipToOutline = true
        }
    }

    private fun setColor(color: Int) {
        mBackgroundColor = color
        invalidate()
    }

    private fun getColor(): Int {
        return mBackgroundColor
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return who === mDrawable || super.verifyDrawable(who)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = mBackgroundColor
        val radius = Math.min(mWidth, mHeight) / 2f
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius, mPaint)
        mDrawable!!.draw(canvas)
    }

    fun toggle(): Boolean {
        if (mAnimatorSet != null) {
            mAnimatorSet?.cancel()
        }
        mAnimatorSet = AnimatorSet()
        val isPlay = mDrawable!!.isPlay()
        val colorAnim = ObjectAnimator.ofInt(this, COLOR, if (isPlay) mPauseBackgroundColor else mPlayBackgroundColor)
        colorAnim.setEvaluator(ArgbEvaluator())
        val pausePlayAnim = mDrawable?.getPausePlayAnimator()
        mAnimatorSet?.interpolator=DecelerateInterpolator()
        mAnimatorSet?.duration=PLAYPAUSEANIMATIONDURATION
        mAnimatorSet?.playTogether(colorAnim, pausePlayAnim)
        mAnimatorSet?.start()
        return isPlay
    }
}