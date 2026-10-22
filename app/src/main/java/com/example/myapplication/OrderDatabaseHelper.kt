package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class OrderDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "orders.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE ${OrderContract.OrderEntry.TABLE_NAME} (
                ${OrderContract.OrderEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${OrderContract.OrderEntry.COLUMN_ITEM_NAME} TEXT NOT NULL,
                ${OrderContract.OrderEntry.COLUMN_QUANTITY} INTEGER NOT NULL,
                ${OrderContract.OrderEntry.COLUMN_PRICE} REAL NOT NULL,
                ${OrderContract.OrderEntry.COLUMN_STATUS} TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${OrderContract.OrderEntry.TABLE_NAME}")
        onCreate(db)
    }
}
