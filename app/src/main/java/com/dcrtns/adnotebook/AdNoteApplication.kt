package com.dcrtns.adnotebook

import android.app.Activity
import android.app.Application
import androidx.preference.PreferenceManager
import com.dcrtns.adnotebook.util.NotificationHelper

class AdNoteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper(applicationContext)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }

    companion object {
        operator fun get(activity: Activity): AdNoteApplication {
            return activity.application as AdNoteApplication
        }
    }
}
