package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classrooms")
data class Classroom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
