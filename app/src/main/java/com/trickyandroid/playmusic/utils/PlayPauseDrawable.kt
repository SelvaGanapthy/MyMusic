package com.trickyandroid.playmusic.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Property
import com.trickyandroid.playmusic.R

class PlayPauseDrawable : Drawable {

    companion object {
        private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t

        private val PROGRESS: Property<PlayPauseDrawable, Float> = object : Property<PlayPauseDrawable, Float>(Float::class.java, "progress") {
            override fun get(v: PlayPauseDrawable):Float {
                return v.getProgress()
            }

            override fun set(v: PlayPauseDrawable, value: Float) {
                v.setProgress( value)
            }
        }
    }

    private val mLeftPauseBar: Path = Path()
    private val mRightPauseBar: Path = Path()
    private val mPaint: Paint = Paint()
    private val mBounds: RectF = RectF()
    private var mPauseBarWidth: Float = 0f
    private var mPauseBarHeight: Float = 0f
    private var mPauseBarDistance: Float = 0f
    private var mWidth: Float = 0f
    private var mHeight: Float = 0f
    private var mProgress: Float = 0f
    private var mIsPlay: Boolean = false

    constructor(context: Context) {
        val res: Resources = context.getResources();
        mPaint.setAntiAlias(true)
        mPaint.setStyle(Paint.Style.FILL)
        mPaint.setColor(Color.WHITE)
        mPauseBarWidth = res.getDimensionPixelSize(R.dimen.pause_bar_width).toFloat()
        mPauseBarHeight = res.getDimensionPixelSize(R.dimen.pause_bar_height).toFloat()
        mPauseBarDistance = res.getDimensionPixelSize(R.dimen.pause_bar_distance).toFloat()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mBounds.set(bounds)
        mWidth = mBounds.width()
        mHeight = mBounds.height()
    }

    override fun draw(canvas: Canvas) {

        mLeftPauseBar.rewind()
        mRightPauseBar.rewind()

        // The current distance between the two pause bars.
        val barDist: Float = lerp(mPauseBarDistance, 0f, mProgress)
        // The current width of each pause bar.
        val barWidth: Float = lerp(mPauseBarWidth, mPauseBarHeight / 2f, mProgress)
        // The current position of the left pause bar's top left coordinate.
        val firstBarTopLeft: Float = lerp(0f, barWidth, mProgress)
        // The current position of the right pause bar's top right coordinate.
        val secondBarTopRight: Float = lerp(2 * barWidth + barDist, barWidth + barDist, mProgress)

        // Draw the left pause bar. The left pause bar transforms into the
        // top half of the play button triangle by animating the position of the
        // rectangle's top left coordinate and expanding its bottom width.
        mLeftPauseBar.moveTo(0f, 0f)
        mLeftPauseBar.lineTo(firstBarTopLeft, -mPauseBarHeight)
        mLeftPauseBar.lineTo(barWidth, -mPauseBarHeight)
        mLeftPauseBar.lineTo(barWidth, 0f)
        mLeftPauseBar.close()

        // Draw the right pause bar. The right pause bar transforms into the
        // bottom half of the play button triangle by animating the position of the
        // rectangle's top right coordinate and expanding its bottom width.
        mRightPauseBar.moveTo(barWidth + barDist, 0f)
        mRightPauseBar.lineTo(barWidth + barDist, -mPauseBarHeight)
        mRightPauseBar.lineTo(secondBarTopRight, -mPauseBarHeight)
        mRightPauseBar.lineTo(2 * barWidth + barDist, 0f)
        mRightPauseBar.close()
        canvas.save()

        // Translate the play button a tiny bit to the right so it looks more centered.
        canvas.translate(lerp(0f, mPauseBarHeight / 8f, mProgress), 0f)

        // (1) Pause --> Play: rotate 0 to 90 degrees clockwise.
        // (2) Play --> Pause: rotate 90 to 180 degrees clockwise.
        val rotationProgress: Float = if (mIsPlay) 1 - mProgress else mProgress
        val startingRotation: Float = if (mIsPlay) 90f else 0f
        canvas.rotate(lerp(startingRotation, startingRotation + 90, rotationProgress), mWidth / 2f, mHeight / 2f)

        // Position the pause/play button in the center of the drawable's bounds.
        canvas.translate(mWidth / 2f - ((2 * barWidth + barDist) / 2f), mHeight / 2f + (mPauseBarHeight / 2f))

        // Draw the two bars that form the animated pause/play button.
        canvas.drawPath(mLeftPauseBar, mPaint)
        canvas.drawPath(mRightPauseBar, mPaint)
        canvas.restore()
    }

    fun getPausePlayAnimator(): Animator {
        val anim: Animator = ObjectAnimator.ofFloat(this, PROGRESS, if (mIsPlay) 1f else 0f, if (mIsPlay) 0f else 1f)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mIsPlay = !mIsPlay
            }
        });
        return anim;
    }

    fun isPlay(): Boolean = mIsPlay

    private fun setProgress(progress: Float) {
        mProgress = progress
        invalidateSelf()
    }

    private fun getProgress(): Float = mProgress

    override fun setAlpha(alpha: Int) {
        mPaint.setAlpha(alpha)
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.setColorFilter(colorFilter)
        invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
