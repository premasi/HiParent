package rk.enkidu.hiparent.ui.splashscreen

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rk.enkidu.hiparent.databinding.ActivityMainBinding
import rk.enkidu.hiparent.ui.authentification.LoginActivity
import rk.enkidu.hiparent.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        /*
        main activity bisa dipakai untuk splashscreen / onboarding
        atau bisa keduanya
         */

        //setup firebase auth
        auth = Firebase.auth


    }

    override fun onStart() {
        super.onStart()
        //check if user already login
        if(auth.currentUser != null){
            CoroutineScope(Dispatchers.Main).launch {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity as Activity)
                        .toBundle()
                )

                finish()
            }
        } else {
            intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity as Activity)
                .toBundle())

            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}