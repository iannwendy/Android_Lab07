package com.example.lab07_exercise1.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.example.lab07_exercise1.data.ContactDatabaseHelper

class ContactProvider : ContentProvider() {
    private lateinit var dbHelper: ContactDatabaseHelper

    companion object {
        const val AUTHORITY = "com.example.lab07_exercise1.provider"
        const val CONTACTS_PATH = "contacts"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$CONTACTS_PATH")

        const val CONTACTS = 1
        const val CONTACT_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, CONTACTS_PATH, CONTACTS)
            addURI(AUTHORITY, "$CONTACTS_PATH/#", CONTACT_ID)
        }

        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.example.lab07_exercise1.contact"
        const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.example.lab07_exercise1.contact"
    }

    override fun onCreate(): Boolean {
        dbHelper = ContactDatabaseHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor?

        when (uriMatcher.match(uri)) {
            CONTACTS -> {
                cursor = db.query(
                    ContactDatabaseHelper.TABLE_CONTACTS,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder ?: "${ContactDatabaseHelper.COLUMN_FIRST_NAME} ASC, ${ContactDatabaseHelper.COLUMN_LAST_NAME} ASC"
                )
            }
            CONTACT_ID -> {
                val id = uri.lastPathSegment
                cursor = db.query(
                    ContactDatabaseHelper.TABLE_CONTACTS,
                    projection,
                    "${ContactDatabaseHelper.COLUMN_ID} = ?",
                    arrayOf(id),
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            CONTACTS -> CONTENT_TYPE
            CONTACT_ID -> CONTENT_ITEM_TYPE
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val match = uriMatcher.match(uri)

        if (match != CONTACTS) {
            throw IllegalArgumentException("Unknown URI: $uri")
        }

        val id = db.insert(ContactDatabaseHelper.TABLE_CONTACTS, null, values)
        context?.contentResolver?.notifyChange(uri, null)
        return Uri.withAppendedPath(CONTENT_URI, id.toString())
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val match = uriMatcher.match(uri)
        val count: Int

        when (match) {
            CONTACTS -> {
                count = db.delete(ContactDatabaseHelper.TABLE_CONTACTS, selection, selectionArgs)
            }
            CONTACT_ID -> {
                val id = uri.lastPathSegment
                count = db.delete(
                    ContactDatabaseHelper.TABLE_CONTACTS,
                    "${ContactDatabaseHelper.COLUMN_ID} = ?",
                    arrayOf(id)
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val match = uriMatcher.match(uri)
        val count: Int

        when (match) {
            CONTACTS -> {
                count = db.update(ContactDatabaseHelper.TABLE_CONTACTS, values, selection, selectionArgs)
            }
            CONTACT_ID -> {
                val id = uri.lastPathSegment
                count = db.update(
                    ContactDatabaseHelper.TABLE_CONTACTS,
                    values,
                    "${ContactDatabaseHelper.COLUMN_ID} = ?",
                    arrayOf(id)
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        context?.contentResolver?.notifyChange(uri, null)
        return count
    }
}

