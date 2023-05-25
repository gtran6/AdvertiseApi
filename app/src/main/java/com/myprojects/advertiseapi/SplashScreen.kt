package com.myprojects.advertiseapi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

private var secondsRemaining: Long = 0L
private const val COUNTER_TIME = 5L

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        createTimer(COUNTER_TIME)
    }

    private fun createTimer(seconds: Long) {
        val counterTextView: TextView = findViewById(R.id.timer)
        val countDownTimer: CountDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000 + 1
                counterTextView.text = "App is loaded in: $secondsRemaining"
            }

            @SuppressLint("RestrictedApi")
            override fun onFinish() {
                secondsRemaining = 5
                counterTextView.text = "Done."

                val application = application as? BaseApplication

                if (application == null) {
                    startMainActivity()
                    return
                }

                // Show the app open ad.
                application.showAdIfAvailable(
                    this@SplashScreen,
                    object : BaseApplication.OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            startMainActivity()
                        }
                    })
            }
        }
        countDownTimer.start()
    }

    fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}