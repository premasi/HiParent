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
import rk.enkidu.hiparent.databinding.FragmentMyPostBinding
import rk.enkidu.hiparent.ui.adapter.DiscussionPrivateAdapter


class MyPostFragment : Fragment() {

    private var _binding: FragmentMyPostBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: DiscussionPrivateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //show loading
        showLoading(true)
        showEmpty(false)

        //setup firebase auth
        auth = Firebase.auth
        db = Firebase.database

        //show data
        showDiscussion()
    }


    private fun showDiscussion() {
        val ref = db.reference.child("Discussion")
        val query = ref.orderByChild("uid").equalTo(auth.currentUser?.uid)
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                showLoading(false)
                if(snapshot.exists()){
                    adapter = DiscussionPrivateAdapter()
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
                    binding?.rvAllDiscussionPrivate?.layoutManager = manager
                    binding?.rvAllDiscussionPrivate?.adapter = adapter
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        showEmpty(true)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //nothing to do
                showLoading(false)
            }

        })
    }

    override fun onResume() {
        showDiscussion()
        super.onResume()
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbAllDiscussPrivate?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showEmpty(isLoading: Boolean){ binding?.tvEmpty?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        CoroutineScope(Dispatchers.Main).cancel()
    }


}