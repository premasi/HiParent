package rk.enkidu.hiparent.ui.authentification

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import rk.enkidu.hiparent.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close loading onCreate
        showLoading(false)

        //close top bar
        setupView()

        //animation
        setupAnimation()
    }

    private fun setupAnimation() {
        val image = ObjectAnimator.ofFloat(binding?.ivIllustration, View.ALPHA, 1F).setDuration(1000)
        val welcomeText = ObjectAnimator.ofFloat(binding?.tvWelcomeTitle, View.ALPHA, 1F).setDuration(500)
        val welcomeDesc = ObjectAnimator.ofFloat(binding?.tvWelcomeDesc, View.ALPHA, 1F).setDuration(500)
        val emailText = ObjectAnimator.ofFloat(binding?.tvEmailText, View.ALPHA, 1F).setDuration(500)
        val emailETL = ObjectAnimator.ofFloat(binding?.etlEmail, View.ALPHA, 1F).setDuration(500)
        val passwordText = ObjectAnimator.ofFloat(binding?.tvPasswordText, View.ALPHA, 1F).setDuration(500)
        val passwordETL = ObjectAnimator.ofFloat(binding?.etlPassword, View.ALPHA, 1F).setDuration(500)
        val toRegister = ObjectAnimator.ofFloat(binding?.tvToRegister, View.ALPHA, 1F).setDuration(500)
        val loginBtn = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(image, welcomeText, welcomeDesc, emailText, emailETL, passwordText, passwordETL, toRegister, loginBtn)
            start()
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

    private fun showLoading(isLoading: Boolean){ binding?.pbLogin?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}