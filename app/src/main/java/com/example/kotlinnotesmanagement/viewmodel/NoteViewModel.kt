package com.example.kotlinnotesmanagement.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinnotesmanagement.data.database.DatabaseHelper
import com.example.kotlinnotesmanagement.data.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NoteViewModel(private val noteDatabaseHelper: DatabaseHelper) : ViewModel() {

    val notes = MutableLiveData<List<Note>>()
    val note = MutableLiveData<Note?>()


    init {
        getAllNotes()
    }

    fun getAllNotes() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val allNotes = noteDatabaseHelper.getAllNotes()
                notes.postValue(allNotes)
            }
        }
    }

    fun getNoteById(noteId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val noteData = noteDatabaseHelper.getNoteById(noteId)
                note.postValue(noteData)
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                noteDatabaseHelper.updateNote(note)
                getAllNotes()
            }
        }
    }


    fun addNote(note: Note) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                noteDatabaseHelper.addNote(note)
                getAllNotes()
            }
        }
    }
    fun DeleteNote(note: Note) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                noteDatabaseHelper.deleteNote(note)
                getAllNotes()
            }
        }
    }
}