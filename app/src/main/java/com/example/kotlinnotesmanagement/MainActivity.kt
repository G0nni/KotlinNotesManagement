package com.example.kotlinnotesmanagement
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinnotesmanagement.data.database.NoteDatabaseHelper
import com.example.kotlinnotesmanagement.data.model.Note
import com.example.kotlinnotesmanagement.ui.NoteCard.NoteCard
import com.example.kotlinnotesmanagement.ui.theme.KotlinNotesManagementTheme
import com.example.kotlinnotesmanagement.viewmodel.NoteViewModel
import com.example.kotlinnotesmanagement.viewmodel.NoteViewModelFactory
import java.util.Date



class MainActivity : ComponentActivity() {

    private lateinit var noteDatabaseHelper: NoteDatabaseHelper
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteDatabaseHelper = NoteDatabaseHelper(this)
        val factory = NoteViewModelFactory(noteDatabaseHelper)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        setContent {
            KotlinNotesManagementTheme {
                Surface() {
                    MainScreen()
                }
            }
        }
        noteViewModel.getAllNotes()
    }

    @Composable
    fun MainScreen() {
        val notes: List<Note> by noteViewModel.notes.observeAsState(initial = emptyList())

        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { addNote() }) {
                Text(text = "Ajouter une note")
            }

            // Afficher la liste des notes
            LazyColumn {
                items(notes) { note ->
                    NoteCard(note) { noteToDelete ->
                        noteViewModel.DeleteNote(noteToDelete)
                    }
                }
            }
        }
    }

    private fun addNote() {
        val note = Note(
            id = System.currentTimeMillis().toString(),
            title = "Titre de la note",
            content = "Contenu de la note",
            category = "Catégorie de la note",
            creationDate = Date(),
            lastModifiedDate = Date()
        )
        noteViewModel.addNote(note)
    }
}
