package com.example.kotlinnotesmanagement

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
import androidx.compose.material3.AlertDialog
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
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinnotesmanagement.data.database.DatabaseHelper
import com.example.kotlinnotesmanagement.data.model.Note
import com.example.kotlinnotesmanagement.ui.theme.KotlinNotesManagementTheme
import com.example.kotlinnotesmanagement.viewmodel.NoteViewModel
import com.example.kotlinnotesmanagement.viewmodel.NoteViewModelFactory
import java.util.Date
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle

enum class NoteCategory(val label: String) {
    PERSONAL("Personnel"),
    WORK("Travail"),
    SHOPPING("Achats"),
    OTHER("Autre")
}

class AddNoteActivity : ComponentActivity() {
    private lateinit var noteDatabaseHelper: DatabaseHelper
    private lateinit var noteViewModel: NoteViewModel

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
                Surface() {
                    val note = noteViewModel.note.observeAsState()
                    if (note.value != null) {
                        AddNoteForm(note.value!!)
                    } else {
                        AddNoteForm()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddNoteForm(note: Note? = null) {
        val (title, setTitle) = remember { mutableStateOf(note?.title ?: "") }
        val (content, setContent) = remember { mutableStateOf(note?.content ?: "") }
        val noteCategories = NoteCategory.values().map { it.label }
        var expanded by remember { mutableStateOf(false) }
        var selectedCategories by remember { mutableStateOf(note?.categories?.toMutableList() ?: mutableListOf<String>()) }
        var showDialog by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = title,
                onValueChange = setTitle,
                label = { Text("Titre") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            OutlinedTextField(
                value = content,
                onValueChange = setContent,
                label = { Text("Contenu") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    BasicTextField(
                        value = TextFieldValue(text = selectedCategories.joinToString(", ")),
                        onValueChange = {},
                        modifier = Modifier.menuAnchor().height(50.dp).width(500.dp).border(width = 1.dp, color = Color.Black),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done, //  n'affiche pas de clavier
                        ),
                        visualTransformation = VisualTransformation.None,
                        readOnly = true,
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
                    )



                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        noteCategories.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    if (!selectedCategories.contains(item)) {
                                        selectedCategories.add(item)
                                    } else {
                                        selectedCategories.remove(item)
                                    }
                                    expanded = false
                                }
                            )
                        }
                    }


                }
            }

            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        val noteToSave = if (note != null) {
                            note.copy(
                                title = title,
                                content = content,
                                categories = selectedCategories,
                                lastModifiedDate = Date()
                            )
                        } else {
                            Note(
                                id = System.currentTimeMillis().toString(),
                                title = title,
                                content = content,
                                categories = selectedCategories,
                                creationDate = Date(),
                                lastModifiedDate = Date()
                            )
                        }
                        if (note != null) {
                            noteViewModel.updateNote(noteToSave)
                        } else {
                            noteViewModel.addNote(noteToSave)
                        }
                        finish()
                    } else {
                        showDialog = true
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = if (note != null) "Modifier la note" else "Ajouter la note")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Erreur") },
                text = { Text("Les champs titre et contenu ne peuvent pas être vides.") },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }




}
