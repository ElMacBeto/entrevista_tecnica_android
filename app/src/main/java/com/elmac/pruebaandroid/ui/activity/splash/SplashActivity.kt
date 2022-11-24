package com.elmac.pruebaandroid.ui.activity.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.elmac.pruebaandroid.MainActivity
import com.elmac.pruebaandroid.R

class SplashActivity : AppCompatActivity() {

    private lateinit var image:ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val displayMetrics = resources.displayMetrics
        val myHeight = displayMetrics.heightPixels/2

        image = findViewById(R.id.splash_image)
        image.layoutParams.height = myHeight

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}