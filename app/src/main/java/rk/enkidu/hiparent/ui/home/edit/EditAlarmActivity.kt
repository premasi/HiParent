package rk.enkidu.hiparent.ui.home.edit

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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.entity.remote.Alarm
import rk.enkidu.hiparent.databinding.ActivityEditAlarmBinding
import rk.enkidu.hiparent.logic.helper.factory.ViewModelFactory
import rk.enkidu.hiparent.logic.helper.picker.DatePickerFragment
import rk.enkidu.hiparent.logic.helper.picker.TimePickerFragment
import rk.enkidu.hiparent.logic.viewmodel.AlarmViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditAlarmActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {

    private var _binding : ActivityEditAlarmBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    //setup viewModel
    private lateinit var alarmViewModel: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditAlarmBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close loading
        showLoading(false)

        //setup firebase auth
        auth = Firebase.auth
        db = Firebase.database

        //setup viewModel
        alarmViewModel = ViewModelProvider(this@EditAlarmActivity, ViewModelFactory.getInstance(auth))[AlarmViewModel::class.java]

        //get data
        val detailData = intent.getParcelableExtra<Alarm>(DATA) as Alarm

        //show data
        showData(detailData)

        //close top bar
        setupView()

        //set date
        setDate()

        //set time
        setTime()

        //upload alarm
        updateAlarm(detailData.id!!)
    }

    private fun showData(detailData: Alarm) {
        binding?.etTitle?.setText(detailData.title)
        binding?.etDesc?.setText(detailData.desc)
        binding?.tvDateText?.text = detailData.date
        binding?.tvTimeText?.text = detailData.time

    }

    private fun updateAlarm(id: String) {
        binding?.btnUpdateAlarm?.setOnClickListener {
            val date = binding?.tvDateText?.text.toString()
            val time = binding?.tvTimeText?.text.toString()
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
                desc.length > 500 -> {
                    binding?.etlDesc?.error = getString(R.string.max_desc_alarm)
                }
                date == getString(R.string.date_fake) -> {
                    Toast.makeText(this@EditAlarmActivity, getString(R.string.date_error), Toast.LENGTH_SHORT).show()
                }
                time == getString(R.string.time_fake) -> {
                    Toast.makeText(this@EditAlarmActivity, getString(R.string.time_error), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    AlertDialog.Builder(this@EditAlarmActivity).apply {
                        setTitle(getString(R.string.alert))
                        setMessage(getString(R.string.update_confirm))
                        setNegativeButton(getString(R.string.no)){ _, _ -> }
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            showLoading(true)
                            alarmViewModel.update(id, date, time, title, desc)

                            Toast.makeText(this@EditAlarmActivity, getString(R.string.alarm_update_success), Toast.LENGTH_SHORT).show()

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setTime() {
        binding?.ivTime?.setOnClickListener {
            val timePickerFragmentOne = TimePickerFragment()
            timePickerFragmentOne.show(supportFragmentManager,
                TIME_PICKER_ONCE_TAG
            )
        }
    }

    private fun setDate() {
        binding?.ivDate?.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // Set text dari textview once
        binding?.tvDateText?.text = dateFormat.format(calendar.time)
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
            TIME_PICKER_ONCE_TAG -> binding?.tvTimeText?.text = dateFormat.format(calendar.time)
        }
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbAddAlarm?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        supportActionBar?.hide()
    }

    companion object {
        const val DATA = "extra_data"

        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }
}