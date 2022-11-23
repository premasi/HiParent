package rk.enkidu.hiparent.ui.home

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.databinding.ActivityHomeBinding
import rk.enkidu.hiparent.ui.home.fragments.HomeFragment
import rk.enkidu.hiparent.ui.home.fragments.ProfileFragment
import rk.enkidu.hiparent.ui.home.fragments.SettingsFragment

class HomeActivity : AppCompatActivity() {

    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //setup firebase auth
        auth = Firebase.auth

        //close top bar
        setupView()

        //change fragment
        setupFragment()

    }

    private fun setupFragment() {
        CoroutineScope(Dispatchers.Main).launch {
            replaceFragment(HomeFragment())
            binding?.bottomNavigationView?.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.bn_home -> replaceFragment(HomeFragment())
                    R.id.bn_profile -> replaceFragment(ProfileFragment())
                    R.id.bn_settings -> replaceFragment(SettingsFragment())
                    else -> {}
                }
                true
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_home, fragment)
        fragmentTransaction.commit()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}