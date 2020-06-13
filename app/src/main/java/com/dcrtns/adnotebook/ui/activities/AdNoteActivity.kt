package com.dcrtns.adnotebook.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.dcrtns.adnotebook.Key
import com.dcrtns.adnotebook.R
import com.dcrtns.adnotebook.base.BaseActivity
import com.dcrtns.adnotebook.database.AdNoteDatasource
import com.dcrtns.adnotebook.models.AdNote
import com.dcrtns.adnotebook.util.DialogUtil
import com.dcrtns.adnotebook.util.EditTextUtil
import kotlinx.android.synthetic.main.activity_create_adnote.*

class AdNoteActivity : BaseActivity() {

    private var mAdNote: AdNote? = null
    private var mIsEditing: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_adnote)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState != null) {
            mAdNote = savedInstanceState.getParcelable(Key.ADNOTE)
            mIsEditing = savedInstanceState.getBoolean(Key.IS_EDITING)
        } else {
            mAdNote = intent.getParcelableExtra(Key.ADNOTE)
            mIsEditing = intent.getBooleanExtra(Key.IS_EDITING, false)
        }

        EditTextUtil.setupEditText(this, headlineEditText1, textView1, 30)
        EditTextUtil.setupEditText(this, headlineEditText2, textView2, 30)
        EditTextUtil.setupEditText(this, headlineEditText3, textView3, 30)
        EditTextUtil.setupEditText(this, pathEditText1, textView4, 15)
        EditTextUtil.setupEditText(this, pathEditText2, textView5, 15)
        EditTextUtil.setupEditText(this, descriptionEditText, textView6, 90)
        EditTextUtil.setupEditText(this, descriptionEditText2, textView7, 90)

        if (mIsEditing!!) {
            supportActionBar!!.title = getString(R.string.edit_ad)
            loadAdNote(mAdNote!!)
        }
    }

    override fun onBackPressed() {
        if (mIsEditing!!) {
            if (!isEmptyAd()) {
                updateAdNote()
            } else {
                finish()
            }
        } else {
            if (!isEmptyAd()) {
                DialogUtil.cancelAdDialog(this, R.string.cancel_dialog_title, R.string.cancel_ad_dialog_text)
            } else {
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_adnote, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.save_entry_button -> {
                when {
                    mIsEditing!! -> {
                        updateAdNote()
                    }
                    else -> {
                        saveAdNote()
                    }
                }
                return true
            }

            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.save_entry_button)
        item.isEnabled = !isEmptyAd()
        return true
    }

    private fun saveAdNote() {
        mAdNote = AdNote(-1,
                headlineEditText1.text.toString(),
                headlineEditText2.text.toString(),
                headlineEditText3.text.toString(),
                descriptionEditText.text.toString(),
                descriptionEditText2.text.toString(),
                mainUrlEditText.text.toString(),
                pathEditText1.text.toString(),
                pathEditText2.text.toString(),
                null, null,
                campaignEditText.text.toString(),
                adGroupEditText.text.toString(),
                notesEditText.text.toString())

        val datasource = AdNoteDatasource(this)
        datasource.create(mAdNote!!)
        finish()
    }

    private fun updateAdNote() {
        mAdNote = AdNote(
                mAdNote!!.id,
                headlineEditText1.text.toString(),
                headlineEditText2.text.toString(),
                headlineEditText3.text.toString(),
                descriptionEditText.text.toString(),
                descriptionEditText2.text.toString(),
                mainUrlEditText.text.toString(),
                pathEditText1.text.toString(),
                pathEditText2.text.toString(),
                null, null,
                campaignEditText.text.toString(),
                adGroupEditText.text.toString(),
                notesEditText.text.toString())

        val datasource = AdNoteDatasource(this)
        datasource.update(mAdNote!!)
        finish()
    }

    private fun loadAdNote(adNote: AdNote) {
        headlineEditText1.setText(adNote.headline1)
        headlineEditText2.setText(adNote.headline2)
        headlineEditText3.setText(adNote.headline3)
        mainUrlEditText.setText(adNote.mainUrl)
        pathEditText1.setText(adNote.urlPath1)
        pathEditText2.setText(adNote.urlPath2)
        descriptionEditText.setText(adNote.description)
        descriptionEditText2.setText(adNote.description2)
        campaignEditText.setText(adNote.campaignName)
        adGroupEditText.setText(adNote.adGroupName)
        notesEditText.setText(adNote.extraNotes)
    }

    private fun isEmptyAd(): Boolean {
        return (EditTextUtil.hasNoText(headlineEditText1) && EditTextUtil.hasNoText(headlineEditText2) && EditTextUtil.hasNoText(headlineEditText3))
    }

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, AdNoteActivity::class.java)
        }

        fun createIntent(context: Context, adNote: AdNote): Intent {
            val intent = Intent(context, AdNoteActivity::class.java)
            intent.putExtra(Key.ADNOTE, adNote)
            intent.putExtra(Key.IS_EDITING, true)
            return intent
        }
    }
}
