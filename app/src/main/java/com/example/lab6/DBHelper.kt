package com.example.lab6

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_GEOTAG" +
                "($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$KEY_TIME TEXT," +
                "$KEY_LATITUDE TEXT," +
                "$KEY_LONGITUDE TEXT," +
                "$KEY_BEARING TEXT)"

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GEOTAG")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "geotagListDB.db"
        const val TABLE_GEOTAG = "geotagList"
        const val DATABASE_VERSION = 1
        const val KEY_ID = "_id"
        const val KEY_TIME = "time"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_BEARING = "bearing"
    }
}
