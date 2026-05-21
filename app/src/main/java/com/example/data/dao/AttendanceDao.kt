package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Classroom
import com.example.data.model.Student
import com.example.data.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    // Classrooms
    @Query("SELECT * FROM classrooms ORDER BY name ASC")
    fun getAllClassrooms(): Flow<List<Classroom>>

    @Query("SELECT * FROM classrooms ORDER BY name ASC")
    suspend fun getAllClassroomsOnce(): List<Classroom>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassroom(classroom: Classroom): Long

    @Update
    suspend fun updateClassroom(classroom: Classroom)

    @Delete
    suspend fun deleteClassroom(classroom: Classroom)


    // Students
    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY rollNumber ASC, name ASC")
    fun getStudentsByClass(classId: Int): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY rollNumber ASC, name ASC")
    suspend fun getStudentsByClassOnce(classId: Int): List<Student>

    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)


    // Attendance Records
    @Query("SELECT * FROM attendance_records WHERE classId = :classId AND date = :date")
    fun getAttendanceByClassAndDate(classId: Int, date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE classId = :classId AND date = :date")
    suspend fun getAttendanceByClassAndDateOnce(classId: Int, date: String): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(records: List<AttendanceRecord>)

    @Query("DELETE FROM attendance_records WHERE classId = :classId AND date = :date")
    suspend fun clearAttendanceForClassAndDate(classId: Int, date: String)
}
