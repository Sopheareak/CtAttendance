package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance_records",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Classroom::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["classId"]),
        Index(value = ["date", "studentId"], unique = true)
    ]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val classId: Int,
    val date: String, // "YYYY-MM-DD"
    val status: String, // "P" (Present), "A" (Absent Without Permission), "E" (Excuse/Permission)
    val remarks: String? = null
)
