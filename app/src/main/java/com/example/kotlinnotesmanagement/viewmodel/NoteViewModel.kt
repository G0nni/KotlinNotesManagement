package com.example.kotlinnotesmanagement.viewmodel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinnotesmanagement.data.database.NoteDatabaseHelper
import com.example.kotlinnotesmanagement.data.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NoteViewModel(private val noteDatabaseHelper: NoteDatabaseHelper) : ViewModel() {

    val notes = MutableLiveData<List<Note>>()

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