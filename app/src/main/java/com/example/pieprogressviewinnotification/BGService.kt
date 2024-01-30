package com.example.pieprogressviewinnotification


import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class BGService : Service() {

    companion object {
        const val NOTIF_ID = 1001
        const val NOTIF_DISMISS_ID = 1002
    }

    val handler by lazy {
        Handler(mainLooper)
    }

    lateinit var handlerRunnable: Runnable
    override fun onBind(p0: Intent?): IBinder? = null
    private val PROGRESS_MAX = 60 //secs
    private var PROGRESS = 0
    private val channelID = "notif_progress"
    private val label = "progress"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent != null) {
            if (intent.action.toString() == NOTIF_DISMISS_ID.toString()) {
                handler.removeCallbacks(handlerRunnable)
                stopForeground(true)
                stopSelf()
            } else {
                startForeground(NOTIF_ID, showNotification())
            }
        }
        return START_NOT_STICKY
    }

    private fun showNotification(): Notification {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            NotificationChannel(channelID, label, importance).apply {
                notificationManager.createNotificationChannel(this)
            }
        }
        val notificationView = RemoteViews(
            packageName,
            R.layout.timer_notification
        )
        val notificationViewBig = RemoteViews(
            packageName,
            R.layout.big_notification_view
        )

        val d = PieProgressDrawable(this)
        d.setColor(ContextCompat.getColor(this, R.color.teal_200))

        val stopNotificationIntent = Intent(this, BGService::class.java)
        stopNotificationIntent.action = NOTIF_DISMISS_ID.toString()
        val dismissIntent = PendingIntent.getService(
            this,
            NOTIF_ID,
            stopNotificationIntent,
            PendingIntent.FLAG_IMMUTABLE // for android 12 support
        )

        var builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setSubText("Flash sale")
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setAutoCancel(false)
            .setCustomContentView(notificationView)
            .setCustomBigContentView(notificationViewBig)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .addAction(
                R.drawable.ic_baseline_stop_circle_24, "DISMISS",
                dismissIntent
            )

        NotificationManagerCompat.from(this).apply {
            handlerRunnable = object : Runnable {
                override fun run() {
                    if (PROGRESS == PROGRESS_MAX + 1) {
                        // timer completes
                        stopSelf()
                        handler.removeCallbacks(this)
                        return
                    }
                    val bit = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                    val can = Canvas(bit)
                    d.setBounds(0, 0, can.width, can.height)
                    d.draw(can)
                    notificationView.setImageViewBitmap(R.id.iv_timer, bit)
                    notificationView.setTextViewText(
                        R.id.tv_timer,
                        "00:" + (PROGRESS_MAX - PROGRESS).toString()
                    )
                    notificationViewBig.setImageViewBitmap(R.id.iv_timer, bit)
                    notificationViewBig.setTextViewText(
                        R.id.tv_timer,
                        "00:" + (PROGRESS_MAX - PROGRESS).toString()
                    )
                    d.level = (PROGRESS * 1.675).toInt()
                    notify(NOTIF_ID, builder.build())
                    PROGRESS += 1
                    builder.setSilent(true)
                    handler.postDelayed(this, 1000) //1 sec delay
                }
            }
            handler.post(handlerRunnable)
        }
        return builder.build()
    }

}

