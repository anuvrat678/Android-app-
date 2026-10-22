package com.example.myapplication

import android.net.Uri
import android.provider.BaseColumns

object OrderContract {
    const val AUTHORITY = "com.example.myapplication.provider"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")
    const val PATH_ORDERS = "orders"

    object OrderEntry : BaseColumns {
        val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDERS).build()

        const val TABLE_NAME = "orders"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_ITEM_NAME = "item_name"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_PRICE = "price"
        const val COLUMN_STATUS = "status"
    }
}
