package rk.enkidu.hiparent.ui.forum.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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

        //setup firebase auth
        auth = Firebase.auth
        db = Firebase.database

        //show data
        showData()
    }

    private fun showData() {
        val ref = db.reference.child("Discussion")
        val manager = LinearLayoutManager(requireActivity())
        binding?.rvAllDiscussion?.layoutManager = manager

        val options = FirebaseRecyclerOptions.Builder<Discussion>()
            .setQuery(ref, Discussion::class.java)
            .build()

        adapter = DiscussionAdapter(options)
        binding?.rvAllDiscussion?.adapter = adapter
        showLoading(false)
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbAllDiscuss?.visibility = if (isLoading) View.VISIBLE else View.GONE }

}