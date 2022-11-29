package rk.enkidu.hiparent.ui.authentification

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private var _binding: ActivityResetPasswordBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close top bar
        setupView()

        //close
        close()

        //reset password
        resetPassword()
    }

    private fun resetPassword() {
        binding?.btnReset?.setOnClickListener {
            val email = binding?.etEmail?.text.toString().trim()
            when{
                email.isEmpty() -> {
                    binding?.etlEmail?.error = getString(R.string.email_is_empty_error)
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding?.etlEmail?.error = getString(R.string.email_not_valid_message)
                }
                else ->  {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(this@ResetPasswordActivity, getString(R.string.success_reset), Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@ResetPasswordActivity, getString(R.string.failed_reset), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


    }

    private fun close() {
        binding?.ivBack?.setOnClickListener {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}