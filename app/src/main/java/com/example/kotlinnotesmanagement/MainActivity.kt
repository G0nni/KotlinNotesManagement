package com.example.kotlinnotesmanagement
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinnotesmanagement.data.database.DatabaseHelper
import com.example.kotlinnotesmanagement.data.model.Note
import com.example.kotlinnotesmanagement.ui.NoteCard.NoteCard
import com.example.kotlinnotesmanagement.ui.theme.KotlinNotesManagementTheme
import com.example.kotlinnotesmanagement.viewmodel.NoteViewModel
import com.example.kotlinnotesmanagement.viewmodel.NoteViewModelFactory



class MainActivity : ComponentActivity() {

    private lateinit var noteDatabaseHelper: DatabaseHelper
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteDatabaseHelper = DatabaseHelper(this)
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
    override fun onResume() {
        super.onResume()
        noteViewModel.getAllNotes()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen() {
        val notes: List<Note> by noteViewModel.notes.observeAsState(initial = emptyList())
        var selectedCategory by remember { mutableStateOf("Toutes") }
        var expanded by remember { mutableStateOf(false) }

        val noteCategories = listOf("Toutes") + NoteCategory.values().map { it.label }
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    Text(
                        text = selectedCategory,
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },

                    ) {
                        Text(
                            text = selectedCategory,
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.widthIn(min = 200.dp)
                        ) {
                            noteCategories.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        selectedCategory = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Button(onClick = { addNote() }) {
                    Text(text = "Ajouter une note")
                }
            }

            LazyColumn {
                items(
                    if (selectedCategory == "Toutes") notes
                    else notes.filter { it.categories.contains(selectedCategory) }
                ) { note ->
                    NoteCard(note, onDelete = { noteToDelete ->
                        noteViewModel.DeleteNote(noteToDelete)
                    }, onNoteClick = { clickedNote ->
                        val intent = Intent(context, NoteDetailActivity::class.java)
                        intent.putExtra("note_id", clickedNote.id)
                        context.startActivity(intent)
                    })
                }
            }
        }
    }

    private fun addNote() {

        val intent = Intent(this, AddNoteActivity::class.java)
        startActivity(intent)

    }

}
