package com.example.kotlinnotesmanagement.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.kotlinnotesmanagement.data.model.Note
import com.example.kotlinnotesmanagement.data.model.User
import java.util.Date

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notes_database"
        private const val DATABASE_VERSION = 1

        // Définition de la structure de la table des utilisateurs
        private const val USER_TABLE_NAME = "users"
        private const val USER_COLUMN_ID = "id"
        private const val USER_COLUMN_USERNAME = "username"
        private const val USER_COLUMN_PASSWORD = "password"

        // Définition de la structure de la table des notes
        private const val NOTE_TABLE_NAME = "notes"
        private const val NOTE_COLUMN_ID = "id"
        private const val NOTE_COLUMN_TITLE = "title"
        private const val NOTE_COLUMN_CONTENT = "content"
        private const val NOTE_COLUMN_CATEGORIES = "categories"
        private const val NOTE_COLUMN_CREATION_DATE = "creation_date"
        private const val NOTE_COLUMN_LAST_MODIFIED_DATE = "last_modified_date"


    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTableQuery = "CREATE TABLE $USER_TABLE_NAME (" +
                "$USER_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$USER_COLUMN_USERNAME TEXT," +
                "$USER_COLUMN_PASSWORD TEXT)"
        db.execSQL(createUserTableQuery)

        val createNoteTableQuery = "CREATE TABLE $NOTE_TABLE_NAME (" +
                "$NOTE_COLUMN_ID TEXT PRIMARY KEY," +
                "$NOTE_COLUMN_TITLE TEXT," +
                "$NOTE_COLUMN_CONTENT TEXT," +
                "$NOTE_COLUMN_CATEGORIES TEXT," +
                "$NOTE_COLUMN_CREATION_DATE INTEGER," +
                "$NOTE_COLUMN_LAST_MODIFIED_DATE INTEGER)"
        db.execSQL(createNoteTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $USER_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $NOTE_TABLE_NAME")
        onCreate(db)
    }

    fun addNote(note: Note) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NOTE_COLUMN_ID, note.id)
        values.put(NOTE_COLUMN_TITLE, note.title)
        values.put(NOTE_COLUMN_CONTENT, note.content)
        values.put(NOTE_COLUMN_CATEGORIES, note.categories.joinToString(",")) // Convert the list to a string
        values.put(NOTE_COLUMN_CREATION_DATE, note.creationDate.time)
        values.put(NOTE_COLUMN_LAST_MODIFIED_DATE, note.lastModifiedDate.time)
        db.insert(NOTE_TABLE_NAME, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $NOTE_TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_CONTENT))
                val categories = cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_CATEGORIES)).split(",") // Convert the string back to a list
                val creationDate = Date(cursor.getLong(cursor.getColumnIndex(NOTE_COLUMN_CREATION_DATE)))
                val lastModifiedDate = Date(cursor.getLong(cursor.getColumnIndex(NOTE_COLUMN_LAST_MODIFIED_DATE)))
                val note = Note(id, title, content, categories, creationDate, lastModifiedDate)
                notesList.add(note)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notesList
    }

    @SuppressLint("Range")
    fun getNoteById(noteId: String): Note? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $NOTE_TABLE_NAME WHERE $NOTE_COLUMN_ID=?", arrayOf(noteId))

        var note: Note? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_CONTENT))
            val categories = cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_CATEGORIES)).split(",") // Convert the string back to a list
            val creationDate = Date(cursor.getLong(cursor.getColumnIndex(NOTE_COLUMN_CREATION_DATE)))
            val lastModifiedDate = Date(cursor.getLong(cursor.getColumnIndex(NOTE_COLUMN_LAST_MODIFIED_DATE)))
            note = Note(id, title, content, categories, creationDate, lastModifiedDate)
        }
        cursor.close()
        db.close()
        return note
    }

    fun updateNote(note: Note) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NOTE_COLUMN_ID, note.id)
        values.put(NOTE_COLUMN_TITLE, note.title)
        values.put(NOTE_COLUMN_CONTENT, note.content)
        values.put(NOTE_COLUMN_CATEGORIES, note.categories.joinToString(",")) // Convert the list to a string
        values.put(NOTE_COLUMN_LAST_MODIFIED_DATE, note.lastModifiedDate.time)
        db.update(NOTE_TABLE_NAME, values, "$NOTE_COLUMN_ID=?", arrayOf(note.id))
        db.close()
    }

    fun deleteNote(note: Note) {
        val db = this.writableDatabase
        db.delete(NOTE_TABLE_NAME, "$NOTE_COLUMN_ID=?", arrayOf(note.id))
        db.close()
    }

    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(USER_COLUMN_USERNAME, user.username)
            put(USER_COLUMN_PASSWORD, user.password)
        }
        return db.insert(USER_TABLE_NAME, null, values)
    }

    @SuppressLint("Range")
    fun getUser(username: String): User? {
        val db = this.readableDatabase
        val selection = "${USER_COLUMN_USERNAME} = ?"
        val selectionArgs = arrayOf(username)
        val cursor: Cursor = db.query(
            USER_TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var user: User? = null // Initialiser l'utilisateur à null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(USER_COLUMN_ID))
            val password = cursor.getString(cursor.getColumnIndex(USER_COLUMN_PASSWORD))
            user = User(id, username, password) // Affecter l'utilisateur avec les valeurs récupérées
        }

        cursor.close() // Fermer le curseur après avoir récupéré les valeurs
        db.close() // Fermer la base de données

        return user
    }

    fun deleteUser(username: String) {
        val db = this.writableDatabase
        db.delete(USER_TABLE_NAME, "${USER_COLUMN_USERNAME} = ?", arrayOf(username))
        db.close()
    }


}
