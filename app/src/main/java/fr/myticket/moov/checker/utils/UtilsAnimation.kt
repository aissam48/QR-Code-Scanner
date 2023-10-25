package fr.myticket.moov.checker.utils

import android.content.Context
import android.view.View
import android.view.animation.*
import androidx.core.view.animation.PathInterpolatorCompat
import fr.myticket.moov.checker.R
import fr.myticket.moov.checker.utils.visible

object UtilsAnimation {


    fun scaleAnim(view: View, startOffset: Long, duration: Long = 500) :ScaleAnimation{
        view.visible()
        val animate = ScaleAnimation(
            -0.3f,
            1f,
            1f,
            1f,
            1f,
            1f
        )
        animate.startOffset = startOffset
        animate.duration = duration
        animate.fillAfter = true
        view.startAnimation(animate)
        return animate
    }

  fun scaleAnim(view: View, startOffset: Long, fromX : Float,duration :Long) {
        view.visible()
        val animate = ScaleAnimation(
            fromX,
            1f,
            0f,
            1f,
            0.5f,
            0.5f
        )
        animate.startOffset = startOffset
        animate.duration = duration
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    // slide the view from below itself to the current position
    fun fadeSlideRightToLeft(view: View, startOffset: Long) {
        view.visible()
        val animate = TranslateAnimation(
            200f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            0f
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        val animateFade = AlphaAnimation(0f, 1f)
        animateFade.duration = 1000
        animateFade.fillAfter = true
        val s1 = AnimationSet(true)
        s1.startOffset = startOffset
        s1.interpolator = OvershootInterpolator()
        s1.addAnimation(animate)
        s1.addAnimation(animateFade)
        view.startAnimation(s1)
    }

    fun fadeSlideTopToBottom(view: View, startOffset: Long) {
        view.visible()
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            -200f,  // fromYDelta
            0f
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        val animateFade = AlphaAnimation(0f, 1f)
        animateFade.duration = 1000
        animateFade.fillAfter = true
        val s1 = AnimationSet(true)
        s1.startOffset = startOffset
        s1.interpolator = OvershootInterpolator()
        s1.addAnimation(animate)
        s1.addAnimation(animateFade)
        view.startAnimation(s1)
    }

    // slide the view from below itself to the current position
    fun fadeSlideRight(view: View, startOffset: Long) {
        view.visible()
        val animate = TranslateAnimation(
            100f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            0f
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        val animateFade = AlphaAnimation(0f, 1f)
        animateFade.duration = 800
        animateFade.fillAfter = true
        val s1 = AnimationSet(true)
        s1.startOffset = startOffset
        s1.interpolator = OvershootInterpolator()
        s1.addAnimation(animate)
        s1.addAnimation(animateFade)
        view.startAnimation(s1)
    }

    fun startWavesAnimation(view: View, context: Context, amplitude: Double, frequency: Double) {
        val myAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_up)
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        val interpolator: Interpolator =
            PathInterpolatorCompat.create(1.000f, 0.000f, 1.000f, 1.030f )
        myAnim.interpolator = interpolator
        view.startAnimation(myAnim)
    }

    fun fadeOut(view: View, startOffset: Long , duration: Long = 500 ,   animationEnd : (animationEnded : Boolean) -> Unit) {
        view.visible()
        val animateFade = AlphaAnimation(0f, 1f)
        animateFade.duration = duration
        animateFade.fillAfter = true
        val s1 = AnimationSet(true)
        s1.startOffset = startOffset
        s1.interpolator = AccelerateInterpolator()

        s1.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                animationEnd.invoke(true)
            }

            override fun onAnimationRepeat(animation: Animation?) {
             }

        })
        s1.addAnimation(animateFade)
        view.startAnimation(s1)
    }

    fun fadeOut(view: View, startOffset: Long, duration: Long = 500) {
        view.visibility = View.VISIBLE
        val animateFade = AlphaAnimation(0f, 1f)
        animateFade.duration = duration
        animateFade.fillAfter = true
        val s1 = AnimationSet(true)
        s1.startOffset = startOffset
        s1.interpolator = AccelerateInterpolator()
        s1.addAnimation(animateFade)
        view.startAnimation(s1)
    }

    fun fadeIn(view: View, visibility: Int, startOffset: Long, duration: Long = 500) {
        val animateFade = AlphaAnimation(1f, 0f)
        animateFade.duration = duration
        animateFade.fillAfter = true
        val s1 = AnimationSet(true)
        s1.interpolator = AccelerateInterpolator()
        s1.addAnimation(animateFade)
        s1.startOffset = startOffset
        s1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                view.visibility = visibility
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
        view.startAnimation(s1)

    }

    fun fadeIn(view: View , startOffset: Long , duration: Long = 1000 , animationEnd : (animationEnded : Boolean)-> Unit) {
        val animateFade = AlphaAnimation(1f, 0f)
        animateFade.duration = duration
        animateFade.fillAfter = true
        val s1 = AnimationSet(true)
        s1.interpolator = AccelerateInterpolator()
        s1.addAnimation(animateFade)
        s1.startOffset = startOffset
        s1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                animationEnd.invoke(true)
             }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
        view.startAnimation(s1)

    }

    fun slideUp(view: View, startOffset: Long, duration: Long) {
        view.visible()
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            1000f,  // fromYDelta
            0f // toYDelta
        )
        animate.duration = duration
        animate.fillAfter = true


        val s1 = AnimationSet(true)
        s1.startOffset = startOffset
        s1.interpolator = LinearInterpolator()
        s1.addAnimation(animate)
        view.startAnimation(s1)
    }

    fun slideDown(view: View,   startOffset: Long, duration: Long , animationEnd : ( animationEnded : Boolean)->Unit) {
        view.visible()
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            500f // toYDelta
        )
        animate.duration = duration
        animate.fillAfter = true

        val animateFade = AlphaAnimation(1f, 0f)
        animateFade.startOffset = 300
        animateFade.duration = 100
        animateFade.fillAfter = true

        val s1 = AnimationSet(true)
        s1.startOffset = startOffset
        s1.interpolator = LinearInterpolator()
        s1.addAnimation(animate)
        s1.addAnimation(animateFade)
        s1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {

                animationEnd.invoke(true )
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
        view.startAnimation(s1)
    }

//    fun startButtonAnimation(view: View, context: Context) {
//        val myAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
//        // Use bounce interpolator with amplitude 0.2 and frequency 20
//        val interpolator = BounceInterpolator(0.30, 25.0)
//        myAnim.interpolator = interpolator
//        view.startAnimation(myAnim)
//    }
}