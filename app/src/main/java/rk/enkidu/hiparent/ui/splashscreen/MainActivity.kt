package rk.enkidu.hiparent.ui.splashscreen

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.app.ActivityOptionsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import rk.enkidu.hiparent.databinding.ActivityMainBinding
import rk.enkidu.hiparent.ui.home.HomeActivity
import rk.enkidu.hiparent.ui.onboarding.OnBoardingActivity

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close top bar
        setupView()

        //setup firebase auth
        auth = Firebase.auth


    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        supportActionBar?.hide()
    }


    override fun onStart() {
        super.onStart()
        //check if user already login
        if(auth.currentUser != null){
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity as Activity)
                        .toBundle()
                )
                delay(4000)
                finish()
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                val intent = Intent(this@MainActivity, OnBoardingActivity::class.java)
                startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity as Activity)
                        .toBundle()
                )
                delay(4000)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        CoroutineScope(Dispatchers.Main).cancel()
    }

}