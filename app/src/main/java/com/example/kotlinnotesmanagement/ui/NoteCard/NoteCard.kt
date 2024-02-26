package com.example.kotlinnotesmanagement.ui.NoteCard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlinnotesmanagement.data.model.Note

@Composable
fun NoteCard(note: Note, onDelete: (Note) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = note.title, modifier = Modifier.weight(1f))
                Column {
                    note.categories.forEach { category ->
                        Text(text = category)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = note.content)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onDelete(note) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Supprimer")
            }
        }
    }
}