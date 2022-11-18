package rk.enkidu.hiparent.ui.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import rk.enkidu.hiparent.databinding.ActivityHomeBinding
import rk.enkidu.hiparent.ui.authentification.LoginActivity

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

        binding?.btnLogout?.setOnClickListener {
            auth.signOut()

            Toast.makeText(this@HomeActivity, "logout berhasil", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@HomeActivity as Activity).toBundle())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}