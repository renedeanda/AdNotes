package com.dcrtns.adnotebook.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import com.dcrtns.adnotebook.R

/**
 * Created by renedeanda on 1/30/18.
 */

class NotificationHelper
/**
 * Registers notification channels, which can be used later by individual notifications.
 *
 * @param context The application context
 */
(context: Context) : ContextWrapper(context) {
    private var manager: NotificationManager? = null

    init {

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            val chan1 = NotificationChannel(ANNOUNCEMENT_CHANNEL,
                    getString(R.string.announcements), NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableVibration(false)
                enableLights(false)
            }
            getManager()!!.createNotificationChannel(chan1)
        }
    }

    private fun getManager(): NotificationManager? {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager
    }

    companion object {
        val ANNOUNCEMENT_CHANNEL = "announcements"
    }


}
