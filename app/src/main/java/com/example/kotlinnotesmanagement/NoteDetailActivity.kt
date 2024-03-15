package com.example.kotlinnotesmanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinnotesmanagement.data.database.DatabaseHelper
import com.example.kotlinnotesmanagement.data.model.Note
import com.example.kotlinnotesmanagement.ui.theme.KotlinNotesManagementTheme
import com.example.kotlinnotesmanagement.viewmodel.NoteViewModel
import com.example.kotlinnotesmanagement.viewmodel.NoteViewModelFactory
import java.util.Date
class NoteDetailActivity : ComponentActivity() {
    private lateinit var noteDatabaseHelper: DatabaseHelper
    private lateinit var noteViewModel: NoteViewModel
    override fun onResume() {
        super.onResume()
        val noteId = intent.getStringExtra("note_id")
        if (noteId != null) {
            noteViewModel.getNoteById(noteId)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteDatabaseHelper = DatabaseHelper(this)
        val factory = NoteViewModelFactory(noteDatabaseHelper)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]



        val noteId = intent.getStringExtra("note_id")
        if (noteId != null) {
            noteViewModel.getNoteById(noteId)
        }

        setContent {
            KotlinNotesManagementTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val note = noteViewModel.note.observeAsState()
                    if (note.value != null) {
                        NoteDetail(note.value!!)
                    }
                }
            }
        }
    }

    @Composable
    fun NoteDetail(note: Note) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "Title", modifier = Modifier.padding(16.dp))
            Text(text = note.title, modifier = Modifier.padding(16.dp).fillMaxWidth())

            Text(text = "Content", modifier = Modifier.padding(16.dp))
            Text(text = note.content, modifier = Modifier.padding(16.dp).fillMaxWidth())

            Text(text = "Categories", modifier = Modifier.padding(16.dp))
            Text(text = note.categories.joinToString(", "), modifier = Modifier.padding(16.dp).fillMaxWidth())

            Button(
                onClick = {
                    noteViewModel.DeleteNote(note)
                    finish()
                },
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Text(text = "Delete")
            }

            Button(
                onClick = {
                    val intent = Intent(this@NoteDetailActivity, AddNoteActivity::class.java)
                    intent.putExtra("note_id", note.id)
                    startActivity(intent)
                },
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Text(text = "Edit")
            }
        }
    }
}