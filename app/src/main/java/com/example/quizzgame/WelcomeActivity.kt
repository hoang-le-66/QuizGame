package com.example.quizzgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.quizzgame.databinding.ActivityWelcomeBinding
import com.google.android.material.animation.AnimationUtils

class WelcomeActivity : AppCompatActivity() {
    lateinit var splashBinding : ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view = splashBinding.root
        setContentView(view)
        //To param
        //1st: application context
        //2nd: define the path of anim we created
        val alphaAnimation = android.view.animation.AnimationUtils.loadAnimation(applicationContext,R.anim.splash_anim)
        //Apply the animation to textView
        splashBinding.tvSplash.startAnimation(alphaAnimation)

        val handler = Handler(Looper.getMainLooper())
        //1st: Object of the runnable class
        //2nd: Duration of the delay
        //You cant create an object from interface Runnable directly
        handler.postDelayed(object : Runnable{
            override fun run() {
                //Code of the transactions that we want to be done
                val intent = Intent(this@WelcomeActivity,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            //Hold the process 5s then execute the code in the "run" function
        },3000)
    }
}