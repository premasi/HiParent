@file:Suppress("DEPRECATION")

package rk.enkidu.hiparent.ui.forum.add.home

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.preferences.SettingsPreferences
import rk.enkidu.hiparent.databinding.ActivityHomeBinding
import rk.enkidu.hiparent.logic.helper.factory.SettingsViewModelFactory
import rk.enkidu.hiparent.logic.viewmodel.SettingsViewModel
import rk.enkidu.hiparent.ui.forum.add.home.fragments.AlarmFragment
import rk.enkidu.hiparent.ui.forum.add.home.fragments.HomeFragment
import rk.enkidu.hiparent.ui.forum.add.home.fragments.ProfileFragment
import rk.enkidu.hiparent.ui.forum.add.home.fragments.SettingsFragment

class HomeActivity : AppCompatActivity() {

    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    private var connectivity: ConnectivityManager? = null
    private var info: NetworkInfo? = null

    //setting theme
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //setting theme
        settingTheme()

        //setup firebase auth
        auth = Firebase.auth

        //close top bar
        setupView()

        //change fragment
        setupFragment()

        //check internet
        checkInternet()

    }

    private fun checkInternet() {
        connectivity = this@HomeActivity.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(connectivity != null){
            info = connectivity!!.activeNetworkInfo

            if(info != null){
                if(info!!.state == NetworkInfo.State.CONNECTED){
                    //do nothing
                }
            } else {
                Toast.makeText(this@HomeActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun settingTheme(){
        val pref = SettingsPreferences.getInstance(dataStore)
        val settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory(pref))[SettingsViewModel::class.java]

        settingsViewModel.getThemeSettings().observe(this){
            if(it){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setupFragment() {
        CoroutineScope(Dispatchers.Main).launch {
            replaceFragment(HomeFragment())
            binding?.bottomNavigationView?.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.bn_home -> replaceFragment(HomeFragment())
                    R.id.bn_alarm -> replaceFragment(AlarmFragment())
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
        CoroutineScope(Dispatchers.Main).cancel()
    }
}