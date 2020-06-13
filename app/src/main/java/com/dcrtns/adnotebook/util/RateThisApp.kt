package com.dcrtns.adnotebook.util

import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.dcrtns.adnotebook.R
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit

object RateThisApp {
    //TODO refactor KEY constants for PREFs so can access elsewhere in app
    private val TAG = RateThisApp::class.java.simpleName
    private const val PREF_NAME = "RateThisApp"
    private const val KEY_INSTALL_DATE = "rta_install_date"
    private const val KEY_LAUNCH_TIMES = "rta_launch_times"
    private const val KEY_OPT_OUT = "rta_opt_out"
    private const val KEY_ASK_LATER_DATE = "rta_ask_later_date"
    private var mInstallDate = Date()
    private var mLaunchTimes = 0
    private var mOptOut = false
    private var mAskLaterDate = Date()
    private var sConfig = Config()
    private var sCallback: Callback? = null

    // Weak ref to avoid leaking the context
    private var sDialogRef: WeakReference<AlertDialog>? = null

    /**
     * If true, print LogCat - change when debugging
     */
    private const val DEBUG = false

    /**
     * Initialize RateThisApp configuration.
     * @param config Configuration object.
     */
    fun init(config: Config?) {
        if (config != null) {
            sConfig = config
        }
    }

    /**
     * Set callback instance.
     * The callback will receive yes/no/later events.
     * @param callback
     */
    fun setCallback(callback: Callback?) {
        sCallback = callback
    }

    /**
     * Call this API when the launcher activity is launched.<br></br>
     * It is better to call this API in onCreate() of the launcher activity.
     * @param context Context
     */
    fun onCreate(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()
        // If it is the first launch, save the date in shared preference.
        if (pref.getLong(KEY_INSTALL_DATE, 0) == 0L) {
            storeInstallDate(context, editor)
        }
        // Increment launch times
        var launchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0)
        launchTimes++
        editor.putInt(KEY_LAUNCH_TIMES, launchTimes)
        log("Launch times; $launchTimes")
        editor.apply()
        mInstallDate = Date(pref.getLong(KEY_INSTALL_DATE, 0))
        mLaunchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0)
        mOptOut = pref.getBoolean(KEY_OPT_OUT, false)
        mAskLaterDate = Date(pref.getLong(KEY_ASK_LATER_DATE, 0))
        printStatus(context)
    }

    /**
     * Show the rate dialog if the criteria is satisfied.
     * @param context Context
     * @return true if shown, false otherwise.
     */
    fun showRateDialogIfNeeded(context: Context?): Boolean {
        return if (shouldShowRateDialog()) {
            showRateDialog(context)
            true
        } else {
            false
        }
    }

    /**
     * Show the rate dialog if the criteria is satisfied.
     * @param context Context
     * @param themeId Theme ID
     * @return true if shown, false otherwise.
     */
    fun showRateDialogIfNeeded(context: Context?, themeId: Int): Boolean {
        return if (shouldShowRateDialog()) {
            showRateDialog(context, themeId)
            true
        } else {
            false
        }
    }

    /**
     * Check whether the rate dialog should be shown or not.
     * Developers may call this method directly if they want to show their own view instead of
     * dialog provided by this library.
     * @return
     */
    fun shouldShowRateDialog(): Boolean {
        return if (mOptOut) {
            false
        } else {
            if (mLaunchTimes >= sConfig.mCriteriaLaunchTimes) {
                return true
            }
            val threshold = TimeUnit.DAYS.toMillis(sConfig.mCriteriaInstallDays.toLong()) // msec
            Date().time - mInstallDate.time >= threshold &&
                    Date().time - mAskLaterDate.time >= threshold
        }
    }

    /**
     * Show the rate dialog
     * @param context
     */
    fun showRateDialog(context: Context?) {
        val builder = AlertDialog.Builder(context!!)
        showRateDialog(context, builder)
    }

    /**
     * Show the rate dialog
     * @param context
     * @param themeId
     */
    fun showRateDialog(context: Context?, themeId: Int) {
        val builder = AlertDialog.Builder(context!!, themeId)
        showRateDialog(context, builder)
    }

    /**
     * Stop showing the rate dialog
     * @param context
     */
    fun stopRateDialog(context: Context?) {
        if (context != null) {
            setOptOut(context, true)
        }
    }

    /**
     * Get count number of the rate dialog launches
     * @return
     */
    fun getLaunchCount(context: Context): Int {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getInt(KEY_LAUNCH_TIMES, 0)
    }

    private fun showRateDialog(context: Context, builder: AlertDialog.Builder) {
        if (sDialogRef != null && sDialogRef!!.get() != null) {
            // Dialog is already present
            return
        }
        val titleId = if (sConfig.mTitleId != 0) sConfig.mTitleId else R.string.rta_dialog_title
        val messageId = if (sConfig.mMessageId != 0) sConfig.mMessageId else R.string.rta_dialog_message
        val cancelButtonID = if (sConfig.mCancelButton != 0) sConfig.mCancelButton else R.string.rta_dialog_cancel
        val thanksButtonID = if (sConfig.mNoButtonId != 0) sConfig.mNoButtonId else R.string.rta_dialog_no
        val rateButtonID = if (sConfig.mYesButtonId != 0) sConfig.mYesButtonId else R.string.rta_dialog_ok
        builder.setTitle(titleId)
        builder.setMessage(messageId)
        builder.setCancelable(sConfig.mCancelable)
        builder.setPositiveButton(rateButtonID) { _, _ ->
            sCallback?.onYesClicked()
            val appPackage = context.packageName
            var url: String? = "market://details?id=$appPackage"
            if (!TextUtils.isEmpty(sConfig.mUrl)) {
                url = sConfig.mUrl
            }
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (anfe: ActivityNotFoundException) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)))
            }
            setOptOut(context, true)
        }
        builder.setNeutralButton(cancelButtonID, DialogInterface.OnClickListener { _, _ ->
            sCallback?.onCancelClicked()
            clearSharedPreferences(context)
            storeAskLaterDate(context)
        })
        builder.setNegativeButton(thanksButtonID) { _, _ ->
            sCallback?.onNoClicked()
            setOptOut(context, true)
        }
        builder.setOnCancelListener {
            sCallback?.onCancelClicked()
            clearSharedPreferences(context)
            storeAskLaterDate(context)
        }
        builder.setOnDismissListener { sDialogRef?.clear() }
        sDialogRef = WeakReference(builder.show())
    }

    /**
     * Clear data in shared preferences.<br></br>
     * This API is called when the "Later" is pressed or canceled.
     * @param context
     */
    private fun clearSharedPreferences(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.remove(KEY_INSTALL_DATE)
        editor.remove(KEY_LAUNCH_TIMES)
        editor.apply()
    }

    /**
     * Set opt out flag.
     * If it is true, the rate dialog will never shown unless app data is cleared.
     * This method is called when Yes or No is pressed.
     * @param context
     * @param optOut
     */
    private fun setOptOut(context: Context, optOut: Boolean) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(KEY_OPT_OUT, optOut)
        editor.apply()
        mOptOut = optOut
    }

    /**
     * Store install date.
     * Install date is retrieved from package manager if possible.
     * @param context
     * @param editor
     */
    private fun storeInstallDate(context: Context, editor: SharedPreferences.Editor) {
        var installDate = Date()
        val packMan = context.packageManager
        try {
            val pkgInfo = packMan.getPackageInfo(context.packageName, 0)
            installDate = Date(pkgInfo.firstInstallTime)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        editor.putLong(KEY_INSTALL_DATE, installDate.time)
        log("First install: $installDate")
    }

    /**
     * Store the date the user asked for being asked again later.
     * @param context
     */
    private fun storeAskLaterDate(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putLong(KEY_ASK_LATER_DATE, System.currentTimeMillis())
        editor.apply()
    }

    /**
     * Print values in SharedPreferences (used for debug)
     * @param context
     */
    private fun printStatus(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        log("*** RateThisApp Status ***")
        log("Install Date: " + Date(pref.getLong(KEY_INSTALL_DATE, 0)))
        log("Launch Times: " + pref.getInt(KEY_LAUNCH_TIMES, 0))
        log("Opt out: " + pref.getBoolean(KEY_OPT_OUT, false))
    }

    /**
     * Print log if enabled
     * @param message
     */
    private fun log(message: String) {
        if (DEBUG) {
            Log.v(TAG, message)
        }
    }

    /**
     * RateThisApp configuration.
     */
    /**
     * Constructor with default criteria.
     */
    class Config @JvmOverloads constructor(val mCriteriaInstallDays: Int = 7, val mCriteriaLaunchTimes: Int = 10) {
        var mUrl: String? = null
        var mTitleId = 0
        var mMessageId = 0
        var mYesButtonId = 0
        var mNoButtonId = 0
        var mCancelButton = 0
        var mCancelable = true

        /**
         * Set title string ID.
         * @param stringId
         */
        fun setTitle(@StringRes stringId: Int) {
            mTitleId = stringId
        }

        /**
         * Set message string ID.
         * @param stringId
         */
        fun setMessage(@StringRes stringId: Int) {
            mMessageId = stringId
        }

        /**
         * Set rate now string ID.
         * @param stringId
         */
        fun setYesButtonText(@StringRes stringId: Int) {
            mYesButtonId = stringId
        }

        /**
         * Set no thanks string ID.
         * @param stringId
         */
        fun setNoButtonText(@StringRes stringId: Int) {
            mNoButtonId = stringId
        }

        /**
         * Set cancel string ID.
         * @param stringId
         */
        fun setCancelButtonText(@StringRes stringId: Int) {
            mCancelButton = stringId
        }

        /**
         * Set navigation url when user clicks rate button.
         * Typically, url will be https://play.google.com/store/apps/details?id=PACKAGE_NAME for Google Play.
         * @param url
         */
        fun setUrl(url: String?) {
            mUrl = url
        }

        fun setCancelable(cancelable: Boolean) {
            mCancelable = cancelable
        }
    }

    /**
     * Callback of dialog click event
     */
    interface Callback {
        /**
         * "Rate now" event
         */
        fun onYesClicked()

        /**
         * "No, thanks" event
         */
        fun onNoClicked()

        /**
         * "Later" event
         */
        fun onCancelClicked()
    }
}