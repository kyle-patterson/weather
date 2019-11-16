package com.github.adrianhall.weather.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.github.adrianhall.weather.R
import com.github.adrianhall.weather.ui.main.MainActivity
import kotlin.concurrent.thread

/**
 * Splash Screen.  Main objective for this screen is to do all the initialization necessary
 * to bootstrap the application then pass control to the main activity.
 *
 * Note that there is no `onCreate()` method in this activity.  This is quite deliberate as
 * we want minimal code.  The SplashTheme sets up the UI portion of the application.
 */
class SplashActivity : AppCompatActivity() {
    /**
     * Activity Lifecycle Event Handler
     *
     * When the activity enters the Resumed state, it comes to the foreground, and then the system
     * invokes the onResume() callback. This is the state in which the app interacts with the user.
     * The app stays in this state until something happens to take focus away from the app.
     */
    override fun onResume() {
        super.onResume()
        setContentView(R.layout.splash_screen)

        //All the work needs to be done on a non-UI thread.
        thread(start = true) {
            // Once all the work is done, start the main activity
            runOnUiThread { startMainActivity() }
        }
    }

    /**
     * Starts the main activity (once everything is completed)
     */
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
