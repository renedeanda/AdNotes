package com.dcrtns.adnotebook.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcrtns.adnotebook.R
import com.dcrtns.adnotebook.adapters.AdNoteAdapter
import com.dcrtns.adnotebook.base.BaseActivity
import com.dcrtns.adnotebook.database.AdNoteDatasource
import com.dcrtns.adnotebook.models.AdNote
import com.dcrtns.adnotebook.util.RateThisApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.opencsv.CSVWriter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {
    private var mAdNotes: ArrayList<AdNote>? = null
    private var mAdapter: AdNoteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  Declare a new thread to do a preference check
        val t = Thread(Runnable {
            //  Initialize SharedPreferences
            val getPrefs = PreferenceManager.getDefaultSharedPreferences(baseContext)

            //  Create a new boolean and preference and set it to true
            val isFirstStart = getPrefs.getBoolean("firstStart", true)

            //  If the activity has never started before...
            if (isFirstStart) {

                //  Launch app intro
                val i = Intent(this@MainActivity, IntroActivity::class.java)

                runOnUiThread { startActivity(i) }

                //  Make a new preferences editor
                val e = getPrefs.edit()

                //  Edit preference to make it false because we don't want this to run again
                e.putBoolean("firstStart", false)

                //  Apply changes
                e.apply()
            }
        })

        // Start the thread
        t.start()

        fab.setOnClickListener {
            startActivity(AdNoteActivity.createIntent(this))
        }

        rateAppPopup()
        refreshMainPage()
    }

    private fun refreshMainPage() {
        val datasource = AdNoteDatasource(this)
        mAdNotes = datasource.readDesc()

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        mAdapter = AdNoteAdapter(this, mAdNotes!!)
        recyclerView.adapter = mAdapter

        if (mAdNotes!!.size == 0) {
            recyclerView.visibility = View.GONE
            empty_textview.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            empty_textview.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        refreshMainPage()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }

        if (id == R.id.action_export) {
            if (mAdNotes.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.no_adnotes), Toast.LENGTH_SHORT).show()
            } else {
                confirmExportDialog()
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun currentDay(): String {
        val formatter = SimpleDateFormat("yyMMddHHmm", Locale.getDefault())
        return formatter.format(Date().time)
    }

    //Create CSV File
    private fun createCsvFile(outputStream: OutputStream?) {
        val datasource = AdNoteDatasource(this)
        val formatter = SimpleDateFormat(SIMPLE_DATE_PATTERN, Locale.getDefault())

        if (outputStream != null) {
            val exportPd = com.dcrtns.adnotebook.ui.dialogs.ProgressDialog.progressDialog(this, getString(R.string.exporting))
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    runOnUiThread {
                        exportPd.show()
                    }
                    val outputWriter = OutputStreamWriter(outputStream, Charset.forName("UTF-8"))
                    val writer = CSVWriter(outputWriter)
                    val adNotes = datasource.readDesc()
                    val mHeaders = arrayOf("Created", "Updated", "Headline 1", "Headline 2", "Headline 3", "Description 1", "Description 2", "Main URL", "Path 1", "Path 2", "Campaign", "Ad group", "Extra notes")

                    writer.writeNext(mHeaders)

                    for (adNote in adNotes) {
                        val createDate = java.lang.Long.valueOf(adNote.adCreateDate!!)
                        val createdAt = Date(createDate)
                        val updateDate = java.lang.Long.valueOf(adNote.adUpdateDate!!)
                        val updatedAt = Date(updateDate)

                        val created = formatter.format(createdAt.time)
                        val updated = formatter.format(updatedAt.time)
                        val headlineOne = adNote.headline1
                        val headlineTwo = adNote.headline2
                        val headlineThree = adNote.headline3
                        val description = adNote.description
                        val descriptionTwo = adNote.description2
                        val mainUrl = adNote.mainUrl
                        val pathOne = adNote.urlPath1
                        val pathTwo = adNote.urlPath2
                        val campaign = adNote.campaignName
                        val adGroup = adNote.adGroupName
                        val extraNotes = adNote.extraNotes

                        val mExportAds = arrayOf(created, updated, headlineOne, headlineTwo, headlineThree, description, descriptionTwo, mainUrl, pathOne, pathTwo, campaign, adGroup, extraNotes)
                        writer.writeNext(mExportAds, true)
                    }

                    writer.close()
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, getString(R.string.backup_completed), Toast.LENGTH_LONG).show()
                        exportPd.dismiss()
                    }
                } catch (e: IOException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, getString(R.string.failed_to_save_file), Toast.LENGTH_LONG).show()
                        exportPd.dismiss()
                    }
                }

            }
        }

    }

    //CSV confirm export dialog
    private fun confirmExportDialog() {
        val builder = AlertDialog.Builder(this)
                .setTitle(R.string.confirm_export)
                .setMessage(R.string.create_csv_file_confirm_message)
                .setPositiveButton(R.string.export) { _, _ ->
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "text/csv"
                        putExtra(Intent.EXTRA_TITLE, "adnotes_export_" + currentDay() + ".csv")
                    }
                    startActivityForResult(intent, ACTIVITY_SAVE_CSV_FILE)
                }
                .setNegativeButton(R.string.cancel, null)
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ACTIVITY_SAVE_CSV_FILE -> {
                if (resultCode == Activity.RESULT_OK && data?.data != null) {
                    val fileUri = data.data ?: return
                    val outputStream = contentResolver.openOutputStream(fileUri)
                    if (outputStream != null) {
                        createCsvFile(outputStream)
                    } else {
                        Toast.makeText(this, R.string.failed_to_save_file, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val ACTIVITY_SAVE_CSV_FILE = 222
        private const val SIMPLE_DATE_PATTERN = "MM/dd/yyyy'T'HH:mm"
    }
}
