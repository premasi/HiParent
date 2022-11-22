package rk.enkidu.hiparent.ui.forum

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.databinding.ActivityAddNewDiscussBinding
import java.util.Date

class AddNewDiscussActivity : AppCompatActivity() {

    private var _binding : ActivityAddNewDiscussBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddNewDiscussBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close loading
        showLoading(false)

        //setup firebase auth
        auth = Firebase.auth
        db = Firebase.database

        //close top bar
        setupView()

        //upload data
        uploadDiscussion(auth, db)
    }

    private fun uploadDiscussion(auth: FirebaseAuth, db: FirebaseDatabase) {
        binding?.btnAddDiscuss?.setOnClickListener {
            val discussRef = db.reference.child("Discussion")

            val title = binding?.etTitle?.text.toString()
            val desc = binding?.etDesc?.text.toString()

            when{
                title.isEmpty() -> {
                    binding?.etlTitle?.error = getString(R.string.title_empty_message)
                }
                title.length > 50 -> {
                    binding?.etlTitle?.error = getString(R.string.input_title)
                }
                desc.isEmpty() -> {
                    binding?.etlDesc?.error = getString(R.string.desc_empty_message)
                }
                desc.length > 1000 -> {
                    binding?.etlDesc?.error = getString(R.string.max_word)
                }
                else -> {
                    val id = discussRef.push().key
                    showLoading(true)
                    val data = Discussion( id,
                        title, desc, auth.currentUser?.displayName.toString(),
                        auth.currentUser?.photoUrl.toString(), Date().time, auth.currentUser?.uid
                    )
                    discussRef.child(id!!).setValue(data){ error,_ ->
                        if(error != null){
                            showLoading(false)
                            Toast.makeText(this@AddNewDiscussActivity, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                        } else {
                            showLoading(false)
                            Toast.makeText(this@AddNewDiscussActivity, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
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

    private fun showLoading(isLoading: Boolean){ binding?.pbAddDiscuss?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}