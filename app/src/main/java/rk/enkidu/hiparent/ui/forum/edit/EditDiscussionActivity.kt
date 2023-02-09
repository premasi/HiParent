@file:Suppress("DEPRECATION")

package rk.enkidu.hiparent.ui.forum.edit

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.databinding.ActivityEditDiscussionBinding
import rk.enkidu.hiparent.logic.helper.factory.ViewModelFactory
import rk.enkidu.hiparent.logic.viewmodel.DiscussionViewModel
import rk.enkidu.hiparent.ui.forum.ForumActivity
import rk.enkidu.hiparent.ui.forum.add.home.HomeActivity

class EditDiscussionActivity : AppCompatActivity() {

    private var _binding : ActivityEditDiscussionBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    private lateinit var discussionViewModel: DiscussionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditDiscussionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close loading
        showLoading(false)

        //setup firebase auth
        auth = Firebase.auth

        //setup viewModel
        discussionViewModel = ViewModelProvider(this@EditDiscussionActivity, ViewModelFactory.getInstance(auth))[DiscussionViewModel::class.java]

        //get data
        val postDetail = intent.getParcelableExtra<Discussion>(DATA) as Discussion

        binding?.etTitle?.setText(postDetail.title)
        binding?.etDesc?.setText(postDetail.desc)

        //close top bar
        setupView()

        //update data
        updateData(postDetail)
        
        //delete data
        deleteData(postDetail.id.toString())

        //close
        close()
    }

    private fun close() {
        binding?.ivBack?.setOnClickListener {
            finish()
        }
    }

    private fun deleteData(id: String) {
        binding?.btnDeleteDiscuss?.setOnClickListener {
            AlertDialog.Builder(this@EditDiscussionActivity).apply {
                setTitle(getString(R.string.alert))
                setMessage(getString(R.string.delete_confirm))
                setNegativeButton(getString(R.string.no)) { _, _ -> }
                setPositiveButton(getString(R.string.yes)) { _, _ ->
                    showLoading(true)
                    discussionViewModel.deletePost(id)

                    Toast.makeText(this@EditDiscussionActivity, getString(R.string.delete_success), Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.Main).launch {

                        delay(2000)
                        showLoading(false)
                        finish()
                        intent = Intent(this@EditDiscussionActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        intent = Intent(this@EditDiscussionActivity, ForumActivity::class.java)
                        startActivity(intent)
                    }
                }
                create()
                show()
            }
        }
    }


    private fun updateData(data: Discussion) {
        binding?.btnUpdateDiscuss?.setOnClickListener {
            val title = binding?.etTitle?.text.toString()
            val desc = binding?.etDesc?.text.toString()

            when{
                title.isEmpty() -> {
                    binding?.etlTitle?.error = getString(R.string.title_empty_message)
                }
                title.length > 200 -> {
                    binding?.etlTitle?.error = getString(R.string.max_word_t)
                }
                desc.isEmpty() -> {
                    binding?.etlDesc?.error = getString(R.string.desc_empty_message)
                }
                desc.length > 1000 -> {
                    binding?.etlDesc?.error = getString(R.string.max_word)
                }
                else -> {
                    AlertDialog.Builder(this@EditDiscussionActivity).apply {
                        setTitle(getString(R.string.alert))
                        setMessage(getString(R.string.update_confirm))
                        setNegativeButton(getString(R.string.no)){ _, _ -> }
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            showLoading(true)
                            discussionViewModel.updatePost(data.id!!, title, desc)

                            Toast.makeText(this@EditDiscussionActivity, getString(R.string.update_success_discuss), Toast.LENGTH_SHORT).show()

                            CoroutineScope(Dispatchers.Main).launch {
                                delay(2000)
                                showLoading(false)
                                finish()
                            }
                        }
                        create()
                        show()
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

    private fun showLoading(isLoading: Boolean){ binding?.pbEditDiscuss?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        CoroutineScope(Dispatchers.Main).cancel()
    }

    companion object{
        const val DATA = "extra_data"
    }
}