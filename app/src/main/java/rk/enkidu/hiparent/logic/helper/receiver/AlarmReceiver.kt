package rk.enkidu.hiparent.logic.helper.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import rk.enkidu.hiparent.R
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val title = intent?.getStringExtra(EXTRA_TITLE)
        val message = intent?.getStringExtra(EXTRA_MESSAGE)

        if (title != null && message != null) {
            showNotification(context!!, title, message)
        }

    }

    private fun showNotification(context: Context, title: String, message: String){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000,1000,1000,1000,1000))
            .setSound(alarmSound)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000,1000,1000,1000,1000)

            builder.setChannelId(CHANNEL_ID)

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(ONE_TIME_ID, builder.build())
    }

    fun setOntimeAlarm(context: Context, title: String, date: String, time: String, milis: Long, message: String){
        if(isDateInvalid(date, DATE_FORMAT) || isDateInvalid(time, TIME_FORMAT)) return
        val nowMillis = System.currentTimeMillis()
        println("$nowMillis, $milis")

        if (nowMillis < milis) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra(EXTRA_MESSAGE, message)
            intent.putExtra(EXTRA_TITLE, title)

            Log.e("ONE TIME", "$date $time")
            val dateArray = date.split("-").toTypedArray()
            val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val calender = Calendar.getInstance()
            calender.set(Calendar.YEAR, Integer.parseInt(dateArray[0]))
            calender.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
            calender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]))
            calender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
            calender.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
            calender.set(Calendar.SECOND, 0)


            val pendingIntent = PendingIntent.getBroadcast(context, milis.toInt(), intent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calender.timeInMillis, pendingIntent)
        }


    }

    private fun isDateInvalid(date: String, timeFormat: String): Boolean {
        return try{
            val df = SimpleDateFormat(timeFormat, Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        } catch (e: Exception){
            true
        }
    }

    companion object{
        const val CHANNEL_ID = "channel_id"
        const val CHANNEL_NAME = "Alarm Manager Channel"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_TITLE = "extra_title"

        private const val ONE_TIME_ID = 100

        //tanggal
        private const val DATE_FORMAT = "yyyy-MM-dd"
        private const val TIME_FORMAT = "HH:mm"
    }
}