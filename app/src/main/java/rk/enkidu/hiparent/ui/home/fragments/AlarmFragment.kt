package rk.enkidu.hiparent.ui.home.fragments

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
import rk.enkidu.hiparent.ui.home.add.AddAlarmActivity

class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    private lateinit var adapter: AlarmAdapter

    private lateinit var alarmViewModel: AlarmViewModel

    private lateinit var alarmReceiver: AlarmReceiver

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
        alarmViewModel = ViewModelProvider(viewModelStore, ViewModelFactory.getInstance(auth))[AlarmViewModel::class.java]

        //go to add schedule
        goToAdd()

        //show data
        showData()

        //fetch data to alarm
//        fetchData()

    }

//    private fun fetchData() {
//        alarmViewModel.fetchAlarm()
//
//        alarmViewModel.data.observe(requireActivity()){
//            if (it != null){
//                for(i in it.indices){
//                    val uidRemote = it[i].uid
//                    val title = it[i].title
//                    val date = it[i].date
//                    val time = it[i].time
//
//                    if (uidRemote == auth.currentUser?.uid){
//                        sendToRemote(title, date, time)
//                    }
//                }
//            }
//        }
//    }

//    private fun sendToRemote(title: String?, date: String?, time: String?) {
//        alarmReceiver.setOntimeAlarm(requireActivity(), AlarmReceiver.TYPE_ONE_TIME,  date!!, time!!, title!!)
//    }

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

                        //send to alarm
                        alarmReceiver.setOntimeAlarm(context!!, AlarmReceiver.TYPE_ONE_TIME, someData?.date!!,
                            someData.time!!, someData.title!!)

                        val list = ArrayList<Alarm>()
                        list.add(someData)

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

    private fun goToAdd(){
        binding?.btnAddNewAlarm?.setOnClickListener {
            val intent = Intent(activity, AddAlarmActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        showData()
//        fetchData()
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbAlarm?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun showEmpty(isLoading: Boolean){ binding?.tvEmpty?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        CoroutineScope(Dispatchers.Main).cancel()
    }

}