package pe.edu.idat.mozitaapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import pe.edu.idat.mozitaapp.R

class SplashActivity : AppCompatActivity() {

    companion object {
        const val SPLASH_DELAY: Long = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                goHome()
            } else {
                goLogin()
            }


        }, SPLASH_DELAY)
    }

    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun goLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}