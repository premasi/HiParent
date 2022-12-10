package rk.enkidu.hiparent.ui.forum.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import rk.enkidu.hiparent.data.entity.remote.Discussion
import rk.enkidu.hiparent.databinding.FragmentAllPostBinding
import rk.enkidu.hiparent.ui.adapter.DiscussionAdapter

class AllPostFragment : Fragment() {

    private var _binding : FragmentAllPostBinding? = null
    private val binding get() = _binding

    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var adapter: DiscussionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //active loading
        showLoading(true)
        showEmpty(false)

        //setup firebase auth
        auth = Firebase.auth
        db = Firebase.database

        //show data
        showData()
    }

    private fun showData() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            showLoading(false)
        }

        val ref = db.reference.child("Discussion")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    adapter = DiscussionAdapter()
                    showEmpty(false)

                    for(data in snapshot.children){
                        val someData = data.getValue(Discussion::class.java)

                        val list = ArrayList<Discussion>()
                        list.add(someData!!)

                        adapter.setList(list)
                    }

                    val manager = LinearLayoutManager(activity)
                    manager.reverseLayout = true
                    manager.stackFromEnd = true
                    binding?.rvAllDiscussion?.layoutManager = manager
                    binding?.rvAllDiscussion?.adapter = adapter
                } else {

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        showEmpty(true)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //nothing to do
            }

        })
    }

    override fun onResume() {
        showData()
        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        CoroutineScope(Dispatchers.Main).cancel()
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbAllDiscuss?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showEmpty(isLoading: Boolean){ binding?.tvEmpty?.visibility = if (isLoading) View.VISIBLE else View.GONE }
}