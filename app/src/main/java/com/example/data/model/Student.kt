package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = Classroom::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["classId"])]
)
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val classId: Int,
    val name: String,
    val gender: String, // "ប្រុស" (Male) or "ស្រី" (Female)
    val rollNumber: Int = 0
)
