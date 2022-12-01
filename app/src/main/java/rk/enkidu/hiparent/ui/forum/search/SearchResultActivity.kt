package rk.enkidu.hiparent.ui.forum.search

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.databinding.ActivitySearchResultBinding
import rk.enkidu.hiparent.ui.adapter.DiscussionAdapter

class SearchResultActivity : AppCompatActivity() {

    private var _binding : ActivitySearchResultBinding? = null
    private val binding get() = _binding

    private lateinit var db : FirebaseDatabase

    private lateinit var adapter: DiscussionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //show loading
        showLoading(true)
        showEmpty(false)

        //setup firebase auth
        db = Firebase.database

        //close top bar
        setupView()

        //close
        close()

        //get data
        val title = intent.getStringExtra(QUERY)

        //show data
        showData(title!!)
    }

    private fun showData(title: String) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            showLoading(false)
        }

        val ref = db.reference.child("Discussion")
        val query = ref.orderByChild("title").equalTo(title)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    adapter = DiscussionAdapter()

                    for (data in snapshot.children){
                        val someData = data.getValue(Discussion::class.java)

                        val list = ArrayList<Discussion>()
                        list.add(someData!!)

                        adapter.setList(list)
                    }

                    val manager = LinearLayoutManager(this@SearchResultActivity)
                    manager.reverseLayout = true
                    manager.stackFromEnd = true
                    binding?.rvSearch?.layoutManager = manager
                    binding?.rvSearch?.adapter = adapter
                } else {
                    showEmpty(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //do nothing
            }

        })
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

    private fun showLoading(isLoading: Boolean){ binding?.pbSearch?.visibility = if (isLoading) View.VISIBLE else View.GONE }
    private fun showEmpty(isLoading: Boolean){ binding?.tvEmpty?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    companion object{
        const val QUERY = "extra_query"
    }
}