package com.dcrtns.adnotebook.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class AdNoteSQLiteHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(CREATE_ADNOTES)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            database.execSQL(ALTER_ADD_HEADLINE_THREE)
            database.execSQL(ALTER_ADD_DESCRIPTION_TWO)
        }
    }

    companion object {
        private val DB_NAME = "adnotes.db"
        //Changed to 2 for adding 3rd headline & 2nd description
        private val DB_VERSION = 2

        //Table Names
        val ADNOTES_TABLE = "ADNOTES"

        //COLUMN NAMES
        val COLUMN_HEADLINE_ONE = "HEADLINE_ONE"
        val COLUMN_HEADLINE_TWO = "HEADLINE_TWO"
        val COLUMN_HEADLINE_THREE = "HEADLINE_THREE"
        val COLUMN_DESCRIPTION = "DESCRIPTION"
        val COLUMN_DESCRIPTION_TWO = "DESCRIPTION_TWO"
        val COLUMN_MAIN_URL = "MAIN_URL"
        val COLUMN_PATH_ONE = "PATH_ONE"
        val COLUMN_PATH_TWO = "PATH_TWO"
        val COLUMN_CAMPAIGN_NAME = "CAMPAIGN_NAME"
        val COLUMN_ADGROUP_NAME = "ADGROUP_NAME"
        val COLUMN_EXTRA_NOTES = "EXTRA_NOTES"
        val COLUMN_CREATE_DATE = "CREATE_DATE"
        val COLUMN_UPDATE_DATE = "UPDATE_DATE"

        private val CREATE_ADNOTES = (
                "CREATE TABLE " + ADNOTES_TABLE + " ("
                        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_HEADLINE_ONE + " TEXT,"
                        + COLUMN_HEADLINE_TWO + " TEXT,"
                        + COLUMN_DESCRIPTION + " TEXT,"
                        + COLUMN_MAIN_URL + " TEXT,"
                        + COLUMN_PATH_ONE + " TEXT,"
                        + COLUMN_PATH_TWO + " TEXT,"
                        + COLUMN_CREATE_DATE + " INTEGER,"
                        + COLUMN_UPDATE_DATE + " INTEGER,"
                        + COLUMN_CAMPAIGN_NAME + " TEXT,"
                        + COLUMN_ADGROUP_NAME + " TEXT,"
                        + COLUMN_EXTRA_NOTES + " TEXT,"
                        + COLUMN_HEADLINE_THREE + " TEXT,"
                        + COLUMN_DESCRIPTION_TWO + " TEXT)")

        //alter statements for adding new columns
        private val ALTER_ADD_HEADLINE_THREE = "ALTER TABLE $ADNOTES_TABLE ADD COLUMN $COLUMN_HEADLINE_THREE TEXT"
        private val ALTER_ADD_DESCRIPTION_TWO = "ALTER TABLE $ADNOTES_TABLE ADD COLUMN $COLUMN_DESCRIPTION_TWO TEXT"

    }
}
