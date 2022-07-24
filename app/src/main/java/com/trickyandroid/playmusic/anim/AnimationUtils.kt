package com.trickyandroid.playmusic.anim

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView

object AnimationUtils {
    fun animateSunblind(holder: RecyclerView.ViewHolder, goesDown: Boolean) {
        val holderHeight = holder.itemView.height
        holder.itemView.pivotY = (if (goesDown) 0 else holderHeight).toFloat()
        holder.itemView.pivotX = holder.itemView.height.toFloat()
        val animatorSet = AnimatorSet()
        val animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY", if (goesDown) 300f else -300f, 0f)
        val animatorRotation = ObjectAnimator.ofFloat(holder.itemView, "rotationX", if (goesDown) -90f else 90f, 0f)
        val animatorScaleX = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0.5f, 1f)
        animatorSet.playTogether(animatorTranslateY, animatorRotation, animatorScaleX)
        animatorSet.interpolator = DecelerateInterpolator(1.1f)
        animatorSet.duration = 1000
        animatorSet.start()
    }
}