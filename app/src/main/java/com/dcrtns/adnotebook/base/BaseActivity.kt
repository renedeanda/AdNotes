package com.dcrtns.adnotebook.base

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.dcrtns.adnotebook.Key
import com.dcrtns.adnotebook.R
import com.dcrtns.adnotebook.util.RateThisApp
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    protected var sharedPref: SharedPreferences? = null
    protected var mIsDarkTheme: Boolean = false
    protected lateinit var mToolbar: Toolbar

    override fun setContentView(@LayoutRes layoutResID: Int) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        mIsDarkTheme = sharedPref!!.getBoolean(Key.THEME_KEY, false)
        if (mIsDarkTheme) {
            setTheme(R.style.AppThemeDark)
        } else {
            setTheme(R.style.AppThemeLight)
        }
        super.setContentView(layoutResID)

        mToolbar = toolbar_actionbar as Toolbar
        setSupportActionBar(mToolbar)
    }

    fun rateAppPopup() {
        RateThisApp.onCreate(this)
        val config = RateThisApp.Config(3, 4)
        RateThisApp.init(config)
        RateThisApp.showRateDialogIfNeeded(this)
    }
}