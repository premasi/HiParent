package rk.enkidu.hiparent.ui.authentification

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.result.Result
import rk.enkidu.hiparent.databinding.ActivityLoginBinding
import rk.enkidu.hiparent.logic.helper.factory.ViewModelFactory
import rk.enkidu.hiparent.logic.viewmodel.LoginViewModel
import rk.enkidu.hiparent.ui.forum.add.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding

    private lateinit var auth : FirebaseAuth

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close loading onCreate
        showLoading(false)

        //setup firebase auth
        auth = Firebase.auth

        //setup viewmodel
        loginViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(auth))[LoginViewModel::class.java]

        //close top bar
        setupView()

        //animation
        setupAnimation()

        //go to registration page
        toRegister()

        //go to reset page
        toReset()

        //login user
        setupLogin()

    }

    private fun toReset() {
        binding?.tvToReset?.setOnClickListener {
            intent = Intent(this@LoginActivity, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupLogin() {
        binding?.btnLogin?.setOnClickListener {
            val email = binding?.etEmail?.text.toString().trim()
            val password = binding?.etPassword?.text.toString().trim()

            when{
                email.isEmpty() -> {
                    binding?.etlEmail?.error = getString(R.string.email_is_empty_error)
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding?.etlEmail?.error = getString(R.string.email_not_valid_message)
                }
                password.isEmpty() -> {
                    binding?.etlPassword?.error = getString(R.string.password_is_empty_error)
                }
                password.length < 8 -> {
                    binding?.etlPassword?.error = getString(R.string.password_short_than_8_error)
                }
                else -> {
                    loginViewModel.loginUser(email, password).observe(this@LoginActivity){ result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }
                                is Result.Success -> {
                                    showLoading(false)
                                    Toast.makeText(this@LoginActivity, getString(R.string.login_success_message), Toast.LENGTH_SHORT).show()
                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(2000)
                                        intent =
                                            Intent(this@LoginActivity, HomeActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(
                                            intent,
                                            ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity)
                                                .toBundle()
                                        )
                                    }
                                }
                                is Result.Error -> {
                                    showLoading(false)
                                    Toast.makeText(this@LoginActivity, getString(R.string.login_failed_message), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    private fun toRegister(){
        binding?.tvToRegister?.setOnClickListener {
            intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity).toBundle())
        }
    }

    private fun setupAnimation() {
        val image = ObjectAnimator.ofFloat(binding?.ivIllustration, View.ALPHA, 1F).setDuration(1000)
        val welcomeText = ObjectAnimator.ofFloat(binding?.tvWelcomeTitle, View.ALPHA, 1F).setDuration(500)
        val welcomeDesc = ObjectAnimator.ofFloat(binding?.tvWelcomeDesc, View.ALPHA, 1F).setDuration(500)
        val emailText = ObjectAnimator.ofFloat(binding?.tvEmailText, View.ALPHA, 1F).setDuration(500)
        val emailETL = ObjectAnimator.ofFloat(binding?.etlEmail, View.ALPHA, 1F).setDuration(500)
        val passwordText = ObjectAnimator.ofFloat(binding?.tvPasswordText, View.ALPHA, 1F).setDuration(500)
        val passwordETL = ObjectAnimator.ofFloat(binding?.etlPassword, View.ALPHA, 1F).setDuration(500)
        val toReset = ObjectAnimator.ofFloat(binding?.tvToReset, View.ALPHA, 1F).setDuration(500)
        val toRegister = ObjectAnimator.ofFloat(binding?.tvToRegister, View.ALPHA, 1F).setDuration(500)
        val toRegisterText = ObjectAnimator.ofFloat(binding?.tvTextToRegister, View.ALPHA, 1F).setDuration(500)
        val loginBtn = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(image, welcomeText, welcomeDesc, emailText, emailETL, passwordText, passwordETL, toReset, loginBtn, toRegisterText, toRegister)
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