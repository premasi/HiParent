package rk.enkidu.hiparent.ui.forum.add.home.add

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rk.enkidu.hiparent.R
import rk.enkidu.hiparent.data.entity.remote.Alarm
import rk.enkidu.hiparent.databinding.ActivityAddAlarmBinding
import rk.enkidu.hiparent.logic.helper.picker.DatePickerFragment
import rk.enkidu.hiparent.logic.helper.picker.TimePickerFragment
import rk.enkidu.hiparent.ui.forum.add.home.HomeActivity
import java.text.SimpleDateFormat
import java.util.*

class AddAlarmActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {

    private var _binding: ActivityAddAlarmBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    private var timeMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddAlarmBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //close loading
        showLoading(false)

        //setup firebase auth
        auth = Firebase.auth
        db = Firebase.database

        //close top bar
        setupView()

        //set date
        setDate()

        //set time
        setTime()

        //upload alarm
        uploadAlarm()

        //close
        close()
    }

    private fun close() {
        binding?.ivBack?.setOnClickListener {
            finish()
        }
    }

    private fun uploadAlarm() {
        binding?.btnAddAlarm?.setOnClickListener {
            val ref = db.reference.child("Alarm")

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
                    Toast.makeText(this@AddAlarmActivity, getString(R.string.date_error), Toast.LENGTH_SHORT).show()
                }
                time == getString(R.string.time_fake) -> {
                    Toast.makeText(this@AddAlarmActivity, getString(R.string.time_error), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val id = ref.push().key
                    showLoading(true)

                    val alarm = Alarm(
                        id, date, time, timeMillis, title, desc, auth.currentUser?.uid
                    )
                    ref.child(id!!).setValue(alarm){ error,_ ->
                        if(error != null){
                            showLoading(false)
                            Toast.makeText(this@AddAlarmActivity, getString(R.string.add_alarm_fail), Toast.LENGTH_SHORT).show()
                        } else {
                            showLoading(false)
                            Toast.makeText(this@AddAlarmActivity, getString(R.string.add_alarm_success), Toast.LENGTH_SHORT).show()
                            intent = Intent(this@AddAlarmActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }

                    }

                }
            }
        }
    }

    private fun setTime() {
        binding?.ivTime?.setOnClickListener {
            val timePickerFragmentOne = TimePickerFragment()
            timePickerFragmentOne.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
        }
    }

    private fun setDate() {
        binding?.ivDate?.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
        timeMillis = calendar.timeInMillis

        when (tag) {
            TIME_PICKER_ONCE_TAG -> binding?.tvTimeText?.text = dateFormat.format(calendar.time)
            else -> {

            }
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
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }
}