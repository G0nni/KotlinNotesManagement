package com.example.kotlinnotesmanagement.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.kotlinnotesmanagement.data.model.Note
import java.util.Date

class NoteDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notes_database"
        private const val DATABASE_VERSION = 1

        // Définition de la structure de la table des notes
        private const val TABLE_NAME = "notes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_CREATION_DATE = "creation_date"
        private const val COLUMN_LAST_MODIFIED_DATE = "last_modified_date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID TEXT PRIMARY KEY," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_CONTENT TEXT," +
                "$COLUMN_CATEGORY TEXT," +
                "$COLUMN_CREATION_DATE INTEGER," +
                "$COLUMN_LAST_MODIFIED_DATE INTEGER)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addNote(note: Note) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ID, note.id)
        values.put(COLUMN_TITLE, note.title)
        values.put(COLUMN_CONTENT, note.content)
        values.put(COLUMN_CATEGORY, note.category)
        values.put(COLUMN_CREATION_DATE, note.creationDate.time)
        values.put(COLUMN_LAST_MODIFIED_DATE, note.lastModifiedDate.time)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
                val category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY))
                val creationDate = Date(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATION_DATE)))
                val lastModifiedDate = Date(cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_MODIFIED_DATE)))
                val note = Note(id, title, content, category, creationDate, lastModifiedDate)
                notesList.add(note)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notesList
    }

    fun deleteNote(note: Note) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(note.id))
        db.close()
    }

    // Ajoutez d'autres méthodes pour mettre à jour, supprimer des notes, etc. selon vos besoins
}
