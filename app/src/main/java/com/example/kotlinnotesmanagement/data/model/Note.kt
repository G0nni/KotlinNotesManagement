package com.example.kotlinnotesmanagement.data.model

import java.util.Date

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val categories: List<String>,
    val creationDate: Date,
    val lastModifiedDate: Date
)