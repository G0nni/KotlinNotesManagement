package com.example.kotlinnotesmanagement.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.kotlinnotesmanagement.data.model.User

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notes_database"
        private const val DATABASE_VERSION = 1

        // Définition de la structure de la table des utilisateurs
        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_PASSWORD TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_PASSWORD, user.password)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    @SuppressLint("Range")
    fun getUser(username: String): User? {
        val db = this.readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf(username)
        val cursor: Cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var user: User? = null // Initialiser l'utilisateur à null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
            user = User(id, username, password) // Affecter l'utilisateur avec les valeurs récupérées
        }

        cursor.close() // Fermer le curseur après avoir récupéré les valeurs
        db.close() // Fermer la base de données

        return user
    }

    fun deleteUser(username: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_USERNAME = ?", arrayOf(username))
        db.close()
    }
}
