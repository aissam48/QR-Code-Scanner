package fr.myticket.moov.checker.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import fr.myticket.moov.checker.utils.goForwardAnimation
import fr.myticket.moov.checker.databinding.ActivitySplashBinding
import fr.myticket.moov.checker.ui.login.LoginActivity
import fr.myticket.moov.checker.utils.Preferences
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    private var callTimes = 0

    var startValue = 0F
    var endValue = 1.5F

    @Inject
    lateinit var dataStore: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }


    private fun initUI() {

        val animatorSet = AnimatorSet()
        val objectAnimatorX = ObjectAnimator.ofFloat(binding.ivLogo, "scaleX", startValue, endValue)
        objectAnimatorX.duration = 1000

        val objectAnimatorY = ObjectAnimator.ofFloat(binding.ivLogo, "scaleY", startValue, endValue)
        objectAnimatorY.duration = 1000

        animatorSet.playTogether(objectAnimatorX, objectAnimatorY)
        animatorSet.start()

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                callTimes++

                startValue = endValue
                endValue = 1F
                initUI()

                if (callTimes == 2) {
                    val apiKey = dataStore.getApiKey()
                    val token = dataStore.getToken()
                    if (apiKey?.isNotEmpty() == true && token?.isNotEmpty() == true) {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        goForwardAnimation()
                        finish()
                    } else {
                        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                        startActivity(intent)
                        goForwardAnimation()
                        finish()
                    }
                }
            }
        })
    }
}