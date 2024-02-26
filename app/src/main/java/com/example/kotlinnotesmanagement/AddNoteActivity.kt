package com.example.kotlinnotesmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinnotesmanagement.data.database.DatabaseHelper
import com.example.kotlinnotesmanagement.data.model.Note
import com.example.kotlinnotesmanagement.ui.theme.KotlinNotesManagementTheme
import com.example.kotlinnotesmanagement.viewmodel.NoteViewModel
import com.example.kotlinnotesmanagement.viewmodel.NoteViewModelFactory
import java.util.Date

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

        setContent {
            KotlinNotesManagementTheme {
                Surface() {
                    Column {
                        AddNoteForm()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddNoteForm() {
        val (title, setTitle) = remember { mutableStateOf("") }
        val (content, setContent) = remember { mutableStateOf("") }
        val noteCategories = NoteCategory.values().map { it.label }
        var expanded by remember { mutableStateOf(false) }
        var selectedCategories by remember { mutableStateOf(mutableListOf<String>()) }

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
                    modifier = Modifier.menuAnchor()
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
                val note = Note(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    content = content,
                    categories = selectedCategories,
                    creationDate = Date(),
                    lastModifiedDate = Date()
                )
                noteViewModel.addNote(note)
                finish()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Ajouter la note")
        }
    }


}
