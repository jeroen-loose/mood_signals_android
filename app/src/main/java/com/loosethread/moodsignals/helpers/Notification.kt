package com.loosethread.moodsignals.helpers

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.loosethread.moodsignals.MainActivity
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.database.Db
import java.util.Calendar


class Notification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        send(context, intent!!)
    }

    companion object {
        const val CHANNEL_ID = "channel_mood_signals"
        val pendingIntents : MutableList<PendingIntent> = mutableListOf()

        fun send(context: Context, intent: Intent) {
            val pendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.fragmentToday)
                .setArguments(intent.extras)
                .setComponentName(MainActivity::class.java)
                .createPendingIntent()

            var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.smile)
                .setContentTitle(intent.getStringExtra("descriptionText"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array&lt;out String&gt;,
                    //                                        grantResults: IntArray)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    return@with
                }
                // notificationId is a unique int for each notification that you must define.
                notify(intent.getIntExtra("id", 0), builder.build())
            }
        }


        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.channel_name)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance)
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun scheduleNotifications(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val notifications = Db.getNotificationTimes()

            for (notification in notifications) {
                val intent = Intent(context, Notification::class.java)
                intent.putExtra("notification_time_id", notification.id)
                intent.putExtra("descriptionText", notification.question)

                // Create a PendingIntent for the broadcast
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    notification.id!!,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                pendingIntents.add(pendingIntent)

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, notification.time!!.substring(0, 2).toInt())
                calendar.set(Calendar.MINUTE, notification.time!!.substring(3, 5).toInt())
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }

                val time = calendar.timeInMillis

                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    pendingIntent
                )
            }
        }
    }
}