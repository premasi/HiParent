package rk.enkidu.hiparent.ui.authentification

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.databinding.ActivityRegistrationBinding
import rk.enkidu.hiparent.logic.helper.factory.ViewModelFactory
import rk.enkidu.hiparent.logic.viewmodel.RegistrationViewModel
import rk.enkidu.hiparent.data.result.Result

class RegistrationActivity : AppCompatActivity() {

    private var _binding : ActivityRegistrationBinding? = null
    private val binding get() = _binding

    private lateinit var auth : FirebaseAuth

    private lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close loading onCreate
        showLoading(false)


        //setup firebase auth
        auth = Firebase.auth

        //viewModel
        registrationViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(auth))[RegistrationViewModel::class.java]

        //close top bar
        setupView()

        //animation
        setupAnimation()

        //back to login
        backToLogin()

        //create user
        setupUser()
    }

    private fun setupUser() {
        binding?.btnSignup?.setOnClickListener {
            val fullname = binding?.etFullname?.text.toString()
            val email = binding?.etEmailRegis?.text.toString().trim()
            val password = binding?.etPasswordRegis?.text.toString().trim()
            val confirmPassword = binding?.etConfirmPasswordRegis?.text.toString().trim()

            when{
                fullname.isEmpty() -> {
                    binding?.etlFullname?.error = getString(R.string.fullname_empty_error)
                }
                fullname.length > 30 -> {
                    binding?.etlFullname?.error = getString(R.string.max_fullname_char)
                }
                email.isEmpty() -> {
                    binding?.etlEmailRegis?.error = getString(R.string.email_is_empty_error)
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding?.etlEmailRegis?.error = getString(R.string.email_not_valid_message)
                }
                password.isEmpty() -> {
                    binding?.etlPasswordRegis?.error = getString(R.string.password_is_empty_error)
                }
                password.length < 8 -> {
                    binding?.etlPasswordRegis?.error = getString(R.string.password_short_than_8_error)
                }
                confirmPassword.isEmpty() -> {
                    binding?.etlConfirmPasswordRegis?.error = getString(R.string.confirm_is_empty_error)
                }
                confirmPassword != password -> {
                    binding?.etlConfirmPasswordRegis?.error = getString(R.string.confirm_not_equal_password_error)
                }
                else -> {

                    registrationViewModel.createUser(fullname, email, password).observe(this@RegistrationActivity){ result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }
                                is Result.Success -> {
                                    showLoading(false)
                                    Toast.makeText(this@RegistrationActivity, getString(R.string.create_success_message), Toast.LENGTH_SHORT).show()
                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(2000)
                                        finish()
                                    }
                                }
                                is Result.Error -> {
                                    showLoading(false)
                                    Toast.makeText(this@RegistrationActivity, getString(R.string.create_failed_message), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                }
            }
        }

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
            playSequentially(advertise, fullnameText, fullnameETL, emailText, emailETL, passwordText, passwordETL,
                confirmPasswordText, confirmPasswordETL,
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