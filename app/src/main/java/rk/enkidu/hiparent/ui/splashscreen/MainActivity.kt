package rk.enkidu.hiparent.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import rk.enkidu.hiparent.databinding.ActivityMainBinding
import rk.enkidu.hiparent.ui.authentification.LoginActivity

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        /*
        main activity bisa dipakai untuk splashscreen / onboarding
        atau bisa keduanya
         */

        intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}