package com.example.whatsapp_clone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_otp.*

class SplashActivity : AppCompatActivity() {
val TAG="TESTING"
    val mAuth by lazy{
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_splash)
        super.onCreate(savedInstanceState)



        val mCountertimer=object : CountDownTimer(3000,1000){
            /**
             * Callback fired on regular interval.
             * @param millisUntilFinished The amount of time until finished.
             */
            override fun onFinish() {
                if (mAuth.currentUser!=null){
                    val intent= Intent(this@SplashActivity,MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()

                }
                else{
                    val intent= Intent(this@SplashActivity,LoginActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()

                }

            }
            override fun onTick(millisUntilFinished: Long) {

            }



        }.start()






    }



}