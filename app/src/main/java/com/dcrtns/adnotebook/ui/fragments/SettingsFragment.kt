package com.dcrtns.adnotebook.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dcrtns.adnotebook.Key
import com.dcrtns.adnotebook.R
import com.dcrtns.adnotebook.ui.activities.MainActivity

class SettingsFragment : PreferenceFragmentCompat() {
    private var mListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    private var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        sharedPref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity)
        val themeSwitch: SwitchPreference? = findPreference(Key.THEME_KEY)

        themeSwitch?.onPreferenceChangeListener = androidx.preference.Preference.OnPreferenceChangeListener { _, newValue ->
            requireActivity().finish()
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(mListener)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(mListener)
        super.onPause()
    }

    override fun onPreferenceTreeClick(preference: androidx.preference.Preference): Boolean {

        when (preference.key) {
            Key.INVITE_FRIENDS -> {
                sendInvite()
                return true
            }
            Key.RATING_LINK -> {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.dcrtns.adnotebook")))
                } catch (e: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.dcrtns.adnotebook")))
                }

                return true
            }
            Key.FEEDBACK_LINK -> {
                val feedbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/forms/bC8zXbijL46FId4r2"))
                try {
                    startActivity(feedbackIntent)
                } catch (e: android.content.ActivityNotFoundException) {
                    Toast.makeText(activity, getString(R.string.no_browser_app_installed), Toast.LENGTH_SHORT).show()
                }

                return true
            }
            Key.DELIGHTFUL_LINK -> {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=me.afewthings")))
                } catch (e: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=me.afewthings")))
                }
                return true
            }
            Key.DEV_LINK -> {
                val devIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://renedeanda.com/?utm_source=adnotes_app&utm_medium=android"))
                try {
                    startActivity(devIntent)
                } catch (ex: android.content.ActivityNotFoundException) {
                    Toast.makeText(activity, getString(R.string.no_browser_app_installed), Toast.LENGTH_SHORT).show()
                }
                return true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun sendInvite() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.make_export_text_ads_with_adnotes))
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_adnotes) + ": " + INVITE_LINK)
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, getString(R.string.invite_via)))
    }

    companion object {
        private const val INVITE_LINK = "https://adnotes.page.link/invite"
    }
}
