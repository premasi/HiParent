package rk.enkidu.hiparent.ui.onboarding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.entity.local.OnBoardingData
import rk.enkidu.hiparent.databinding.ActivityOnBoardingBinding
import rk.enkidu.hiparent.ui.adapter.OnBoardingViewPagerAdapter
import rk.enkidu.hiparent.ui.authentification.LoginActivity

class OnBoardingActivity : AppCompatActivity() {

    private var _binding : ActivityOnBoardingBinding? = null
    private val binding get() = _binding

    private lateinit var adapter : OnBoardingViewPagerAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var position = 0
    private lateinit var onBoardingViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close top bar
        setupView()

        //set data
        setData()
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

    private fun setData() {
        val onBoardingData:MutableList<OnBoardingData> = ArrayList()
        val nextonboard = binding?.nextonboard

        onBoardingData.add((OnBoardingData(title = getString(R.string.welcome_boarding_1), desc = getString(
            R.string.welcome_desc_boarding_1), R.drawable.logo3)))
        onBoardingData.add((OnBoardingData(title = getString(R.string.welcome_boarding_2), desc = getString(
            R.string.welcome_desc_boarding_2), R.drawable.img2)))
        onBoardingData.add((OnBoardingData(title = getString(R.string.welcome_boarding_3), desc = getString(
            R.string.welcome_desc_boarding_3), R.drawable.img3)))

        setOnboardingViewPagerAdapter(onBoardingData)

        position = onBoardingViewPager.currentItem

        nextonboard?.setOnClickListener{
            if(position < onBoardingData.size){
                position++
                onBoardingViewPager.currentItem = position
            }

            if (position == onBoardingData.size){
                savePrefData()
                val intent = Intent(applicationContext,LoginActivity::class.java)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@OnBoardingActivity as Activity)
                    .toBundle())
            }

        }

        binding?.tabIndicator?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if (tab.position == onBoardingData.size - 1){
                    nextonboard!!.text = getString(R.string.get_started)
                }else{
                    nextonboard!!.text= getString(R.string.next)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //nothing to do
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //nothing to do
            }
        })
    }

    private fun savePrefData() {
        sharedPreferences = applicationContext.getSharedPreferences("pref",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstTimeRun",true)
        editor.apply()
    }

    private fun setOnboardingViewPagerAdapter(onBoardingData: MutableList<OnBoardingData>) {
        onBoardingViewPager = binding?.screenViewPager2!!
        adapter = OnBoardingViewPagerAdapter(this,onBoardingData)
        onBoardingViewPager.adapter= adapter
        binding?.tabIndicator?.setupWithViewPager(onBoardingViewPager)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}