package rk.enkidu.hiparent.ui.forum.add.home.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
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
import rk.enkidu.hiparent.data.entity.remote.Alarm
import rk.enkidu.hiparent.databinding.FragmentAlarmBinding
import rk.enkidu.hiparent.logic.helper.factory.ViewModelFactory
import rk.enkidu.hiparent.logic.helper.receiver.AlarmReceiver
import rk.enkidu.hiparent.logic.viewmodel.AlarmViewModel
import rk.enkidu.hiparent.ui.adapter.AlarmAdapter
import rk.enkidu.hiparent.ui.forum.add.home.add.AddAlarmActivity

class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    private lateinit var adapter: AlarmAdapter

    private lateinit var alarmReceiver: AlarmReceiver

    private lateinit var alarmViewModel: AlarmViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
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

        //setup alarm receiver
        alarmReceiver = AlarmReceiver()

        //setup viewModel
        alarmViewModel = ViewModelProvider(this@AlarmFragment, ViewModelFactory.getInstance(auth))[AlarmViewModel::class.java]

        //go to add schedule
        goToAdd()

        //show data
        showData()

        //fetch data
        fetchData()
    }

    private fun fetchData() {
        alarmViewModel.fetchAlarm()

        alarmViewModel.data.observe(requireActivity()){
            if(it != null){
                for(i in it.indices){
                    val date = it[i].date
                    val time = it[i].time
                    val title = it[i].title
                    val desc = it[i].desc
                    val uid = it[i].uid
                    val milis = it[i].milis

                    println("get title $title, desc $desc")

                    if(uid == auth.currentUser?.uid){
                        //send to alarm
                        alarmReceiver.setOntimeAlarm(requireActivity(), title!!, date!!,
                            time!!, milis!!, desc!!)
                    }
                }
            }
        }
    }


    private fun showData() {
        val ref = db.reference.child("Alarm")
        val query = ref.orderByChild("uid").equalTo(auth.currentUser?.uid)
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showLoading(false)
                if(snapshot.exists()){
                    adapter = AlarmAdapter()

                    for(data in snapshot.children){
                        val someData = data.getValue(Alarm::class.java)

                        val list = ArrayList<Alarm>()
                        list.add(someData!!)

                        adapter.setList(list)
                    }

                    val manager = LinearLayoutManager(activity)
                    manager.reverseLayout = true
                    manager.stackFromEnd = true
                    binding?.rvActivity?.layoutManager = manager
                    binding?.rvActivity?.adapter = adapter
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
        super.onResume()
        showData()
        fetchData()
    }

    private fun goToAdd(){
        binding?.btnAddNewAlarm?.setOnClickListener {
            val intent = Intent(activity, AddAlarmActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbAlarm?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showEmpty(isLoading: Boolean){ binding?.tvEmpty?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        CoroutineScope(Dispatchers.Main).cancel()
    }

}