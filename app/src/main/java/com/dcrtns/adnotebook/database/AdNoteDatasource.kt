package com.dcrtns.adnotebook.database

import android.app.Activity
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.ADNOTES_TABLE
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_ADGROUP_NAME
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_CAMPAIGN_NAME
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_CREATE_DATE
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_DESCRIPTION
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_DESCRIPTION_TWO
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_EXTRA_NOTES
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_HEADLINE_ONE
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_HEADLINE_THREE
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_HEADLINE_TWO
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_MAIN_URL
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_PATH_ONE
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_PATH_TWO
import com.dcrtns.adnotebook.database.AdNoteSQLiteHelper.Companion.COLUMN_UPDATE_DATE
import com.dcrtns.adnotebook.models.AdNote
import java.util.*

class AdNoteDatasource(context: Activity) {

    private val mSQLiteHelper: AdNoteSQLiteHelper = AdNoteSQLiteHelper(context)

    private fun open(): SQLiteDatabase {
        return mSQLiteHelper.writableDatabase
    }

    private fun openReadable(): SQLiteDatabase {
        return mSQLiteHelper.readableDatabase
    }

    private fun close(database: SQLiteDatabase) {
        database.close()
    }

    //CREATE ADNOTE
    fun create(adNote: AdNote) {
        val database = open()
        database.beginTransaction()

        val newAdNoteDate = Date().time

        val adNoteValues = ContentValues()
        adNoteValues.put(COLUMN_HEADLINE_ONE, adNote.headline1)
        adNoteValues.put(COLUMN_HEADLINE_TWO, adNote.headline2)
        adNoteValues.put(COLUMN_HEADLINE_THREE, adNote.headline3)
        adNoteValues.put(COLUMN_DESCRIPTION, adNote.description)
        adNoteValues.put(COLUMN_DESCRIPTION_TWO, adNote.description2)
        adNoteValues.put(COLUMN_MAIN_URL, adNote.mainUrl)
        adNoteValues.put(COLUMN_PATH_ONE, adNote.urlPath1)
        adNoteValues.put(COLUMN_PATH_TWO, adNote.urlPath2)
        adNoteValues.put(COLUMN_CREATE_DATE, newAdNoteDate)
        adNoteValues.put(COLUMN_UPDATE_DATE, newAdNoteDate)
        adNoteValues.put(COLUMN_CAMPAIGN_NAME, adNote.campaignName)
        adNoteValues.put(COLUMN_ADGROUP_NAME, adNote.adGroupName)
        adNoteValues.put(COLUMN_EXTRA_NOTES, adNote.extraNotes)
        database.insert(ADNOTES_TABLE, null, adNoteValues)

        database.setTransactionSuccessful()
        database.endTransaction()

        close(database)
    }

    //READ ALL ADNOTES
    fun readDesc(): ArrayList<AdNote> {
        val database = open()

        val cursor = database.query(
                ADNOTES_TABLE,
                arrayOf(BaseColumns._ID,
                        COLUMN_HEADLINE_ONE,
                        COLUMN_HEADLINE_TWO,
                        COLUMN_DESCRIPTION,
                        COLUMN_MAIN_URL,
                        COLUMN_PATH_ONE,
                        COLUMN_PATH_TWO,
                        COLUMN_CREATE_DATE,
                        COLUMN_UPDATE_DATE,
                        COLUMN_CAMPAIGN_NAME,
                        COLUMN_ADGROUP_NAME,
                        COLUMN_EXTRA_NOTES,
                        COLUMN_HEADLINE_THREE,
                        COLUMN_DESCRIPTION_TWO),
                null, null, null, null,
                "$COLUMN_UPDATE_DATE DESC")

        val adNotes = ArrayList<AdNote>()
        if (cursor.moveToFirst()) {
            do {
                val adNote = AdNote(
                        getIntFromColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, COLUMN_HEADLINE_ONE),
                        getStringFromColumnName(cursor, COLUMN_HEADLINE_TWO),
                        getStringFromColumnName(cursor, COLUMN_HEADLINE_THREE),
                        getStringFromColumnName(cursor, COLUMN_DESCRIPTION),
                        getStringFromColumnName(cursor, COLUMN_DESCRIPTION_TWO),
                        getStringFromColumnName(cursor, COLUMN_MAIN_URL),
                        getStringFromColumnName(cursor, COLUMN_PATH_ONE),
                        getStringFromColumnName(cursor, COLUMN_PATH_TWO),
                        getStringFromColumnName(cursor, COLUMN_CREATE_DATE),
                        getStringFromColumnName(cursor, COLUMN_UPDATE_DATE),
                        getStringFromColumnName(cursor, COLUMN_CAMPAIGN_NAME),
                        getStringFromColumnName(cursor, COLUMN_ADGROUP_NAME),
                        getStringFromColumnName(cursor, COLUMN_EXTRA_NOTES))
                adNotes.add(adNote)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close(database)
        return adNotes
    }

    //UPDATE ADNOTE
    fun update(adNote: AdNote) {
        val database = open()
        database.beginTransaction()

        val updateDate = Date().time

        val updateAdNoteValues = ContentValues()
        updateAdNoteValues.put(COLUMN_HEADLINE_ONE, adNote.headline1)
        updateAdNoteValues.put(COLUMN_HEADLINE_TWO, adNote.headline2)
        updateAdNoteValues.put(COLUMN_HEADLINE_THREE, adNote.headline3)
        updateAdNoteValues.put(COLUMN_DESCRIPTION, adNote.description)
        updateAdNoteValues.put(COLUMN_DESCRIPTION_TWO, adNote.description2)
        updateAdNoteValues.put(COLUMN_MAIN_URL, adNote.mainUrl)
        updateAdNoteValues.put(COLUMN_PATH_ONE, adNote.urlPath1)
        updateAdNoteValues.put(COLUMN_PATH_TWO, adNote.urlPath2)
        updateAdNoteValues.put(COLUMN_UPDATE_DATE, updateDate)
        updateAdNoteValues.put(COLUMN_CAMPAIGN_NAME, adNote.campaignName)
        updateAdNoteValues.put(COLUMN_ADGROUP_NAME, adNote.adGroupName)
        updateAdNoteValues.put(COLUMN_EXTRA_NOTES, adNote.extraNotes)
        database.update(ADNOTES_TABLE,
                updateAdNoteValues,
                String.format(Locale.ENGLISH, "%s=%d", BaseColumns._ID, adNote.id), null)

        database.setTransactionSuccessful()
        database.endTransaction()
        close(database)
    }

    //DELETE ADNOTE
    fun delete(adNoteId: Int) {
        val database = open()
        database.beginTransaction()

        database.delete(ADNOTES_TABLE,
                String.format(Locale.ENGLISH, "%s=%s", BaseColumns._ID, adNoteId.toString()), null)
        database.setTransactionSuccessful()
        database.endTransaction()
    }

    private fun getIntFromColumnName(cursor: Cursor, columnName: String): Int {
        val columnIndex = cursor.getColumnIndex(columnName)
        return cursor.getInt(columnIndex)
    }

    private fun getStringFromColumnName(cursor: Cursor, columnName: String): String? {
        val columnIndex = cursor.getColumnIndex(columnName)
        return cursor.getString(columnIndex)
    }
}
