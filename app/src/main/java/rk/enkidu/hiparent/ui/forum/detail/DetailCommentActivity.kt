package rk.enkidu.hiparent.ui.forum.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.entity.remote.Message
import rk.enkidu.hiparent.databinding.ActivityDetailCommentBinding
import rk.enkidu.hiparent.logic.helper.ViewModelFactory
import rk.enkidu.hiparent.logic.viewmodel.CommentsViewModel

class DetailCommentActivity : AppCompatActivity() {

    private var _binding: ActivityDetailCommentBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    private lateinit var commentsViewModel: CommentsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailCommentBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close edit and delete
        setState(
            state1 = true,
            state2 = true,
            state3 = false,
            state4 = false,
            state5 = false,
            state6 = false, state7 = false)

        //setup firebase auth
        auth = Firebase.auth

        //setup viewModel
        commentsViewModel = ViewModelProvider(this@DetailCommentActivity, ViewModelFactory.getInstance(auth))[CommentsViewModel::class.java]

        //close top bar
        setupView()

        //get data
        val data = intent.getParcelableExtra<Message>(DATA) as Message

        //show data
        showData(data)

        //close
        close()

        //set status
        status(auth.currentUser?.uid, data.uid)

        //edit
        editComment(data.id!!, data.text!!)

        //delete
        deleteComment(data.id)

    }

    private fun deleteComment(id: String?) {
        binding?.ivDelete?.setOnClickListener {
            AlertDialog.Builder(this@DetailCommentActivity).apply {
                setTitle(getString(R.string.alert))
                setMessage(getString(R.string.confirm_delete_comment))
                setNegativeButton(getString(R.string.no)) { _, _ -> }
                setPositiveButton(getString(R.string.yes)) { _, _ ->
                    commentsViewModel.deleteComment(id!!)

                    Toast.makeText(this@DetailCommentActivity, getString(R.string.delete_comment_message_success), Toast.LENGTH_SHORT).show()
                    finish()
                }
                create()
                show()
            }
        }
    }

    private fun editComment(id: String, text: String) {
        binding?.ivEdit?.setOnClickListener {
            setState(
                state1 = false,
                state2 = false,
                state3 = true,
                state4 = false,
                state5 = false,
                state6 = true,
                state7 = true
            )

            binding?.etComment?.setText(text)

            cancelEdit(text)

            updateComment(id)
        }
    }

    private fun updateComment(id: String) {
        binding?.ivUpdate?.setOnClickListener {
            val text = binding?.etComment?.text.toString()

            if(text.isEmpty()){
                binding?.etComment?.error = getString(R.string.comment_isempty)
            } else {
                commentsViewModel.updateComment(id, text)
                Toast.makeText(this@DetailCommentActivity, getString(R.string.update_text_succes), Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }

    private fun cancelEdit(text: String) {
        binding?.ivClose?.setOnClickListener {
            binding?.etComment?.setText(text)

            setState(
                state1 = true,
                state2 = true,
                state3 = false,
                state4 = true,
                state5 = true,
                state6 = false, state7 = false)
        }

    }

    private fun setState(state1: Boolean, state2: Boolean, state3: Boolean, state4: Boolean, state5: Boolean,
                         state6: Boolean, state7: Boolean){
        showTV(state1)
        showTimeStamp(state2)
        showET(state3)
        showEdit(state4)
        showDelete(state5)
        showSend(state6)
        showClose(state7)
    }

    private fun status(uidRemote: String?, uidLocal: String?) {
        if(uidRemote == uidLocal){
            showEdit(true)
            showDelete(true)
        }
    }

    private fun close() {
        binding?.ivBack?.setOnClickListener {
            finish()
        }
    }

    private fun showData(data: Message) {
        Glide.with(this@DetailCommentActivity)
            .load(data.photo)
            .circleCrop()
            .into(binding?.ivProfile!!)

        binding?.tvFullname?.text = data.name
        binding?.tvComment?.text = data.text
        binding?.tvTimestamp?.text = DateUtils.getRelativeTimeSpanString(data.timestamp!!)
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

    private fun showSend(isLoading: Boolean){ binding?.ivUpdate?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showClose(isLoading: Boolean){ binding?.ivClose?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showEdit(isLoading: Boolean){ binding?.ivEdit?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showDelete(isLoading: Boolean){ binding?.ivDelete?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showET(isLoading: Boolean){ binding?.etComment?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showTV(isLoading: Boolean){ binding?.tvComment?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showTimeStamp(isLoading: Boolean){ binding?.tvTimestamp?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    companion object{
        const val DATA = "extra_data"
    }
}