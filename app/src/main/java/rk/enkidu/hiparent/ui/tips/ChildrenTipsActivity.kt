@file:Suppress("DEPRECATION")

package rk.enkidu.hiparent.ui.tips

import android.app.Service
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
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
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.entity.remote.Tips
import rk.enkidu.hiparent.databinding.ActivityChildrenTipsBinding
import rk.enkidu.hiparent.ui.adapter.TipsAdapter

class ChildrenTipsActivity : AppCompatActivity() {

    private var _binding : ActivityChildrenTipsBinding? = null
    private val binding get() = _binding

    private lateinit var db: FirebaseDatabase

    private lateinit var adapter: TipsAdapter

    private lateinit var id: String

    private var connectivity: ConnectivityManager? = null
    private var info: NetworkInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChildrenTipsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //show loading
        showLoading(true)

        //set firebase auth
        db = Firebase.database

        //get type
        id = intent.getStringExtra(TYPE)!!

        //close
        close()

        //check internet
        checkInternet()

        //close top bar
        setupView()

        //show data
        showData()
    }

    private fun checkInternet() {
        connectivity = this@ChildrenTipsActivity.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(connectivity != null){
            info = connectivity!!.activeNetworkInfo

            if(info != null){
                if(info!!.state == NetworkInfo.State.CONNECTED){
                    //do nothing
                }
            } else {
                Toast.makeText(this@ChildrenTipsActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun close() {
        binding?.ivBack?.setOnClickListener {
            finish()
        }
    }

    private fun showData() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            showLoading(false)
        }

        val ref = db.reference.child(id)
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    adapter = TipsAdapter()

                    for(data in snapshot.children){
                        val someData = data.getValue(Tips::class.java)

                        val list = ArrayList<Tips>()
                        list.add(someData!!)

                        adapter.setList(list)
                    }

                    val manager = LinearLayoutManager(this@ChildrenTipsActivity)
                    manager.reverseLayout = true
                    manager.stackFromEnd = true
                    binding?.rvTips?.layoutManager = manager
                    binding?.rvTips?.adapter = adapter
                } else {
                    Toast.makeText(this@ChildrenTipsActivity, getString(R.string.no_materi), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //nothing to do
            }

        })
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

    override fun onResume() {
        showData()
        super.onResume()
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbShowTips?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val TYPE = "extra_type"
    }
}