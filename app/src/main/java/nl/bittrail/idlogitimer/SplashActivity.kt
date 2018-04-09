package nl.bittrail.idlogitimer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.view.View

public class SplashActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_KEY_USE_DURATION = "USE_DURATION"
        const val SPLASH_DURATION = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var useTimeout = getIntent().getBooleanExtra(SplashActivity.EXTRA_KEY_USE_DURATION, true)
        setContentView(R.layout.activity_splash)

        var layout = findViewById<ConstraintLayout>(R.id.layoutSplash);
        layout.setOnClickListener() {
            routeToStartpage()
        }

        if (useTimeout) {
            scheduleSplashScreen()
        }
    }

    private fun scheduleSplashScreen() {
        Handler().postDelayed( {
            routeToStartpage()
            finish()
        }, SPLASH_DURATION)
    }

    private fun routeToStartpage() {
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent)
    }
}
