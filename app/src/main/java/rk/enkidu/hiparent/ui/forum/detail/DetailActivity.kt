package rk.enkidu.hiparent.ui.forum.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.data.entity.remote.Message
import rk.enkidu.hiparent.databinding.ActivityDetailBinding
import rk.enkidu.hiparent.ui.adapter.CommentAdapter
import java.util.Date

class DetailActivity : AppCompatActivity() {

    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    private lateinit var adapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //show loading
        showLoading(true)

        //close top bar
        setupView()

        //setup firebase auth
        auth = Firebase.auth
        db = Firebase.database

        //get data
        val dataDetail = intent.getParcelableExtra<Discussion>(DATA_DETAIL) as Discussion

        //show data
        showData(dataDetail)
        
        //show comments
        showComments(dataDetail.id.toString())

        //send comment
        sendComment(dataDetail.id.toString())

        //close
        close()
    }

    private fun close() {
        binding?.ivBack?.setOnClickListener {
            finish()
        }
    }

    private fun showComments(id: String) {
        val ref = db.reference.child("Comments")
        val query = ref.orderByChild("discussionId").equalTo(id)
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()){
                   adapter = CommentAdapter()

                   for(data in snapshot.children){
                       val someData = data.getValue(Message::class.java)

                       val list = ArrayList<Message>()
                       list.add(someData!!)

                       adapter.setList(list)
                   }

                   val manager = LinearLayoutManager(this@DetailActivity)
                   binding?.rvMessage?.layoutManager = manager
                   binding?.rvMessage?.adapter = adapter
               }
            }

            override fun onCancelled(error: DatabaseError) {
                //nothing to do
            }

        })
    }

    private fun sendComment(postId: String) {
        binding?.ivSend?.setOnClickListener {
            val ref = db.reference.child("Comments")

            val text = binding?.etMessage?.text.toString()

            if(text.isEmpty()){
                binding?.etMessage?.error = getString(R.string.comment_isempty)
            } else {
                showLoading(true)

                val id = ref.push().key
                val data = Message(
                    id,
                    postId,
                    text,
                    auth.currentUser?.displayName,
                    auth.currentUser?.photoUrl.toString(),
                    Date().time,
                    auth.currentUser?.uid
                )

                ref.child(id!!).setValue(data){ error,_ ->
                    if(error != null){
                        showLoading(false)
                        Toast.makeText(this@DetailActivity, getString(R.string.send_failed), Toast.LENGTH_SHORT).show()
                    } else {
                        showLoading(false)
                        Toast.makeText(this@DetailActivity, getString(R.string.send_success), Toast.LENGTH_SHORT).show()
                        binding?.etMessage?.text = null
                    }
                }
            }
        }
    }

    private fun showData(dataDetail: Discussion) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            showLoading(false)
        }

        Picasso.get().load(dataDetail.photoUrl).into(binding?.ivDetail)
        binding?.tvFullnameDetail?.text = dataDetail.name
        binding?.tvDateDetail?.text = DateUtils.getRelativeTimeSpanString(dataDetail.timestamp!!)
        binding?.tvTitleDetail?.text = dataDetail.title
        binding?.tvDescDetail?.text = dataDetail.desc
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbDetail?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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

    companion object{
        const val DATA_DETAIL = "extra_data"
    }
}