package com.example.myapplication

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class OrderContentProvider : ContentProvider() {

    companion object {
        private const val ORDERS = 100
        private const val ORDER_ID = 101

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(OrderContract.AUTHORITY, OrderContract.PATH_ORDERS, ORDERS)
            addURI(OrderContract.AUTHORITY, "${OrderContract.PATH_ORDERS}/#", ORDER_ID)
        }
    }

    private lateinit var dbHelper: OrderDatabaseHelper

    override fun onCreate(): Boolean {
        context?.let {
            dbHelper = OrderDatabaseHelper(it)
            return true
        }
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor

        when (uriMatcher.match(uri)) {
            ORDERS -> {
                cursor = db.query(
                    OrderContract.OrderEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder ?: "${OrderContract.OrderEntry.COLUMN_ID} DESC"
                )
            }
            ORDER_ID -> {
                val id = ContentUris.parseId(uri)
                cursor = db.query(
                    OrderContract.OrderEntry.TABLE_NAME,
                    projection,
                    "${OrderContract.OrderEntry.COLUMN_ID}=?",
                    arrayOf(id.toString()),
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val match = uriMatcher.match(uri)
        if (match != ORDERS) {
            throw IllegalArgumentException("Insertion not supported for $uri")
        }

        val db = dbHelper.writableDatabase
        val id = db.insert(OrderContract.OrderEntry.TABLE_NAME, null, values)
        if (id > 0) {
            context?.contentResolver?.notifyChange(uri, null)
            return ContentUris.withAppendedId(OrderContract.OrderEntry.CONTENT_URI, id)
        }
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val rowsUpdated: Int

        when (uriMatcher.match(uri)) {
            ORDERS -> {
                rowsUpdated = db.update(OrderContract.OrderEntry.TABLE_NAME, values, selection, selectionArgs)
            }
            ORDER_ID -> {
                val id = ContentUris.parseId(uri)
                val sel = "${OrderContract.OrderEntry.COLUMN_ID}=?"
                val selArgs = arrayOf(id.toString())
                rowsUpdated = db.update(OrderContract.OrderEntry.TABLE_NAME, values, sel, selArgs)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (rowsUpdated > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted: Int

        when (uriMatcher.match(uri)) {
            ORDERS -> {
                rowsDeleted = db.delete(OrderContract.OrderEntry.TABLE_NAME, selection, selectionArgs)
            }
            ORDER_ID -> {
                val id = ContentUris.parseId(uri)
                val sel = "${OrderContract.OrderEntry.COLUMN_ID}=?"
                val selArgs = arrayOf(id.toString())
                rowsDeleted = db.delete(OrderContract.OrderEntry.TABLE_NAME, sel, selArgs)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (rowsDeleted > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            ORDERS -> "vnd.android.cursor.dir/${OrderContract.AUTHORITY}.${OrderContract.PATH_ORDERS}"
            ORDER_ID -> "vnd.android.cursor.item/${OrderContract.AUTHORITY}.${OrderContract.PATH_ORDERS}"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}
