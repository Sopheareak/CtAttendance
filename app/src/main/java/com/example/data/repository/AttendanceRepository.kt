package com.example.data.repository

import com.example.data.dao.AttendanceDao
import com.example.data.model.Classroom
import com.example.data.model.Student
import com.example.data.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

class AttendanceRepository(private val attendanceDao: AttendanceDao) {

    val allClassrooms: Flow<List<Classroom>> = attendanceDao.getAllClassrooms()
    val allStudents: Flow<List<Student>> = attendanceDao.getAllStudents()
    val allAttendanceRecords: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendanceRecords()
    val studentCountFlow: Flow<Int> = attendanceDao.getStudentCountFlow()

    suspend fun insertClassroom(classroom: Classroom): Long {
        return attendanceDao.insertClassroom(classroom)
    }

    suspend fun updateClassroom(classroom: Classroom) {
        attendanceDao.updateClassroom(classroom)
    }

    suspend fun deleteClassroom(classroom: Classroom) {
        attendanceDao.deleteClassroom(classroom)
    }

    fun getStudentsByClass(classId: Int): Flow<List<Student>> {
        return attendanceDao.getStudentsByClass(classId)
    }

    suspend fun getStudentsByClassOnce(classId: Int): List<Student> {
        return attendanceDao.getStudentsByClassOnce(classId)
    }

    suspend fun insertStudent(student: Student): Long {
        return attendanceDao.insertStudent(student)
    }

    suspend fun updateStudent(student: Student) {
        attendanceDao.updateStudent(student)
    }

    suspend fun deleteStudent(student: Student) {
        attendanceDao.deleteStudent(student)
    }

    fun getAttendanceByClassAndDate(classId: Int, date: String): Flow<List<AttendanceRecord>> {
        return attendanceDao.getAttendanceByClassAndDate(classId, date)
    }

    suspend fun getAttendanceByClassAndDateOnce(classId: Int, date: String): List<AttendanceRecord> {
        return attendanceDao.getAttendanceByClassAndDateOnce(classId, date)
    }

    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>> {
        return attendanceDao.getAttendanceByDate(date)
    }

    suspend fun insertAttendanceRecord(record: AttendanceRecord): Long {
        return attendanceDao.insertAttendanceRecord(record)
    }

    suspend fun saveAttendanceRecords(records: List<AttendanceRecord>) {
        attendanceDao.insertAttendanceRecords(records)
    }

    suspend fun clearAttendanceForClassAndDate(classId: Int, date: String) {
        attendanceDao.clearAttendanceForClassAndDate(classId, date)
    }

    suspend fun checkAndPopulateDefaultData() {
        val classrooms = attendanceDao.getAllClassroomsOnce()
        if (classrooms.isEmpty()) {
            // Populate Classrooms
            val classId7 = attendanceDao.insertClassroom(Classroom(name = "ថ្នាក់ទី ៧ អា"))
            val classId8 = attendanceDao.insertClassroom(Classroom(name = "ថ្នាក់ទី ៨ អា"))
            val classId9 = attendanceDao.insertClassroom(Classroom(name = "ថ្នាក់ទី ៩ អា"))

            // Sample Students for 7A
            val students7 = listOf(
                Student(classId = classId7.toInt(), name = "សុខ ម៉ារី", gender = "ស្រី", rollNumber = 1),
                Student(classId = classId7.toInt(), name = "ចាន់ សំណាង", gender = "ប្រុស", rollNumber = 2),
                Student(classId = classId7.toInt(), name = "គឹម ហេង", gender = "ប្រុស", rollNumber = 3),
                Student(classId = classId7.toInt(), name = "លី ស្រីនី", gender = "ស្រី", rollNumber = 4),
                Student(classId = classId7.toInt(), name = "ហេង ដារា", gender = "ប្រុស", rollNumber = 5),
                Student(classId = classId7.toInt(), name = "ម៉ៅ វិច្ឆិកា", gender = "ប្រុស", rollNumber = 6),
                Student(classId = classId7.toInt(), name = "កែវ ធារី", gender = "ស្រី", rollNumber = 7),
                Student(classId = classId7.toInt(), name = "សេង ហួរ", gender = "ប្រុស", rollNumber = 8),
                Student(classId = classId7.toInt(), name = "ទេព សុភ័ក្ត្រ", gender = "ស្រី", rollNumber = 9),
                Student(classId = classId7.toInt(), name = "អ៊ុង ចាន់ធូ", gender = "ប្រុស", rollNumber = 10)
            )

            // Sample Students for 8A
            val students8 = listOf(
                Student(classId = classId8.toInt(), name = "ម៉ៅ សុខា", gender = "ប្រុស", rollNumber = 1),
                Student(classId = classId8.toInt(), name = "លី ដាឡែន", gender = "ស្រី", rollNumber = 2),
                Student(classId = classId8.toInt(), name = "ហ៊ាង គឹមស្រី", gender = "ស្រី", rollNumber = 3),
                Student(classId = classId8.toInt(), name = "អ៊ូ វាសនា", gender = "ប្រុស", rollNumber = 4),
                Student(classId = classId8.toInt(), name = "ស៊ុន ពិសិដ្ឋ", gender = "ប្រុស", rollNumber = 5),
                Student(classId = classId8.toInt(), name = "សុវណ្ណ ធីតា", gender = "ស្រី", rollNumber = 6),
                Student(classId = classId8.toInt(), name = "ចៅ ចាន់វីរៈ", gender = "ប្រុស", rollNumber = 7),
                Student(classId = classId8.toInt(), name = "សុខ ស្រីលីន", gender = "ស្រី", rollNumber = 8),
                Student(classId = classId8.toInt(), name = "ឃុន វឌ្ឍនៈ", gender = "ប្រុស", rollNumber = 9),
                Student(classId = classId8.toInt(), name = "ស៊ិន ម៉ារ៉ាឌី", gender = "ប្រុស", rollNumber = 10)
            )

            // Sample Students for 9A
            val students9 = listOf(
                Student(classId = classId9.toInt(), name = "ម៉ៅ រតនា", gender = "ប្រុស", rollNumber = 1),
                Student(classId = classId9.toInt(), name = "សំរិទ្ធ ស្រីពេជ្រ", gender = "ស្រី", rollNumber = 2),
                Student(classId = classId9.toInt(), name = "ភុំ សុផាត", gender = "ប្រុស", rollNumber = 3),
                Student(classId = classId9.toInt(), name = "ចៀវ វាសនា", gender = "ប្រុស", rollNumber = 4),
                Student(classId = classId9.toInt(), name = "សៀង ស្រីរ័ត្ន", gender = "ស្រី", rollNumber = 5),
                Student(classId = classId9.toInt(), name = "លឹម លីហួរ", gender = "ប្រុស", rollNumber = 6),
                Student(classId = classId9.toInt(), name = "សារី ដេវីដ", gender = "ប្រុស", rollNumber = 7),
                Student(classId = classId9.toInt(), name = "កង ម៉ារីណា", gender = "ស្រី", rollNumber = 8),
                Student(classId = classId9.toInt(), name = "ស៊ឹម សុជាតា", gender = "ស្រី", rollNumber = 9),
                Student(classId = classId9.toInt(), name = "តាំង លាងហេង", gender = "ប្រុស", rollNumber = 10)
            )

            val allStudentsToInsert = students7 + students8 + students9
            for (student in allStudentsToInsert) {
                attendanceDao.insertStudent(student)
            }
        }
    }
}
