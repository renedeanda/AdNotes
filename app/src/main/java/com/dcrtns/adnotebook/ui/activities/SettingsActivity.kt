package com.dcrtns.adnotebook.ui.activities

import android.os.Bundle
import com.dcrtns.adnotebook.R
import com.dcrtns.adnotebook.base.BaseActivity
import com.dcrtns.adnotebook.ui.fragments.SettingsFragment

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, SettingsFragment())
                .commit();
    }
}
