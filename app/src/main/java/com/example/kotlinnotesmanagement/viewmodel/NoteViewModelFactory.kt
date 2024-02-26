package com.example.kotlinnotesmanagement.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinnotesmanagement.data.database.DatabaseHelper

class NoteViewModelFactory(private val noteDatabaseHelper: DatabaseHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDatabaseHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}