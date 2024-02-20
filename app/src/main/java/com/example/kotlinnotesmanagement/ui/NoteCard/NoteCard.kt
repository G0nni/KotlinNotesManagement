package com.example.kotlinnotesmanagement.ui.NoteCard


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlinnotesmanagement.data.model.Note



@Composable
fun NoteCard(note: Note, onDelete: (Note) -> Unit) {
    Card(modifier = Modifier.padding(vertical = 6.dp)) {
        Column {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "${note.title} - ${note.content}"
            )
            Button(onClick = { onDelete(note) }) {
                Text(text = "Supprimer")
            }
        }
    }
}