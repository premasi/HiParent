package rk.enkidu.hiparent.ui.authentification

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import rk.enkidu.hiparent.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private var _binding : ActivityRegistrationBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close loading onCreate
        showLoading(false)

        //close top bar
        setupView()

        //animation
        setupAnimation()

        //back to login
        backToLogin()
    }

    private fun setupAnimation() {
        val advertise = ObjectAnimator.ofFloat(binding?.tvAdvertiseText, View.ALPHA, 1F).setDuration(500)
        val fullnameText = ObjectAnimator.ofFloat(binding?.tvFullnameText, View.ALPHA, 1F).setDuration(500)
        val fullnameETL = ObjectAnimator.ofFloat(binding?.etlFullname, View.ALPHA, 1F).setDuration(500)
        val emailText = ObjectAnimator.ofFloat(binding?.tvEmailTextRegis, View.ALPHA, 1F).setDuration(500)
        val emailETL = ObjectAnimator.ofFloat(binding?.etlEmailRegis, View.ALPHA, 1F).setDuration(500)
        val passwordText = ObjectAnimator.ofFloat(binding?.tvPasswordTextRegis, View.ALPHA, 1F).setDuration(500)
        val passwordETL = ObjectAnimator.ofFloat(binding?.etlPasswordRegis, View.ALPHA, 1F).setDuration(500)
        val confirmPasswordText = ObjectAnimator.ofFloat(binding?.tvConfirmPasswordTextRegis, View.ALPHA, 1F).setDuration(500)
        val confirmPasswordETL = ObjectAnimator.ofFloat(binding?.etlConfirmPasswordRegis, View.ALPHA, 1F).setDuration(500)
        val toLogin = ObjectAnimator.ofFloat(binding?.tvToLogin, View.ALPHA, 1F).setDuration(500)
        val btnRegis = ObjectAnimator.ofFloat(binding?.btnSignup, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(advertise, fullnameText, fullnameETL,
                emailText, emailETL, passwordText, passwordETL, confirmPasswordText, confirmPasswordETL,
                toLogin, btnRegis)
            start()
        }
    }

    private fun backToLogin(){
        binding?.ivBack?.setOnClickListener {
            finish()
        }

        binding?.tvToLogin?.setOnClickListener {
            finish()
        }
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

    private fun showLoading(isLoading: Boolean){ binding?.pbRegis?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}