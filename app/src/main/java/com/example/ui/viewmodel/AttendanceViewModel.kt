package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AttendanceDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.Classroom
import com.example.data.model.Student
import com.example.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttendanceRepository

    init {
        val db = AttendanceDatabase.getDatabase(application)
        repository = AttendanceRepository(db.attendanceDao())
        
        // Check and pre-populate with Khmer students on startup
        viewModelScope.launch {
            repository.checkAndPopulateDefaultData()
            // Set first class as default after data is populated
            val classes = repository.allClassrooms.first()
            if (classes.isNotEmpty()) {
                _selectedClassroom.value = classes.first()
            }
        }
    }

    // Screens: "dashboard", "record", "manage", "reports"
    private val _currentScreen = MutableStateFlow("dashboard")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    // Classrooms
    val classrooms: StateFlow<List<Classroom>> = repository.allClassrooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedClassroom = MutableStateFlow<Classroom?>(null)
    val selectedClassroom: StateFlow<Classroom?> = _selectedClassroom.asStateFlow()

    fun selectClassroom(classroom: Classroom) {
        _selectedClassroom.value = classroom
        // Reset draft attendance map when switching classes
        clearDraftAttendance()
    }

    // Students in Selected Classroom
    private val _studentsInSelectedClass = MutableStateFlow<List<Student>>(emptyList())
    val studentsInSelectedClass: StateFlow<List<Student>> = _studentsInSelectedClass.asStateFlow()

    // Date for Attendance taking
    private val _attendanceDate = MutableStateFlow(getCurrentDateString())
    val attendanceDate: StateFlow<String> = _attendanceDate.asStateFlow()

    fun updateAttendanceDate(dateString: String) {
        _attendanceDate.value = dateString
        // Load existing attendance for this class & date
        loadExistingAttendance()
    }

    // Observe active classroom and date to reload students and existing records
    init {
        viewModelScope.launch {
            combine(_selectedClassroom, _attendanceDate) { classroom, date ->
                Pair(classroom, date)
            }.collect { (classroom, date) ->
                if (classroom != null) {
                    // Update students flow
                    repository.getStudentsByClass(classroom.id).collect { list ->
                        _studentsInSelectedClass.value = list
                        loadExistingAttendance()
                    }
                }
            }
        }
    }

    // Attendance taking - Draft State Map: studentId -> status ("P", "A", "E")
    private val _draftAttendance = MutableStateFlow<Map<Int, AttendanceRecord>>(emptyMap())
    val draftAttendance: StateFlow<Map<Int, AttendanceRecord>> = _draftAttendance.asStateFlow()

    fun markAttendance(studentId: Int, status: String, remarks: String? = null) {
        val classroom = _selectedClassroom.value ?: return
        val date = _attendanceDate.value
        val record = AttendanceRecord(
            studentId = studentId,
            classId = classroom.id,
            date = date,
            status = status,
            remarks = remarks
        )
        val currentDraft = _draftAttendance.value.toMutableMap()
        currentDraft[studentId] = record
        _draftAttendance.value = currentDraft
    }

    fun submitAttendance() {
        val classroom = _selectedClassroom.value ?: return
        val date = _attendanceDate.value
        viewModelScope.launch {
            val recordsToSave = _studentsInSelectedClass.value.map { student ->
                _draftAttendance.value[student.id] ?: AttendanceRecord(
                    studentId = student.id,
                    classId = classroom.id,
                    date = date,
                    status = "P",
                    remarks = null
                )
            }
            if (recordsToSave.isNotEmpty()) {
                repository.saveAttendanceRecords(recordsToSave)
            }
        }
    }

    fun clearDraftAttendance() {
        _draftAttendance.value = emptyMap()
    }

    fun loadExistingAttendance() {
        val classroom = _selectedClassroom.value ?: return
        val date = _attendanceDate.value
        viewModelScope.launch {
            val existing = repository.getAttendanceByClassAndDateOnce(classroom.id, date)
            val draftMap = existing.associateBy { it.studentId }
            _draftAttendance.value = draftMap
        }
    }

    // Reports / Historic Data
    val allAttendanceRecords: StateFlow<List<AttendanceRecord>> = repository.allAttendanceRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentCount: StateFlow<Int> = repository.studentCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // CRUD Classroom
    fun testAddClassroom(name: String) {
        viewModelScope.launch {
            repository.insertClassroom(Classroom(name = name))
        }
    }

    fun updateClassroom(classroom: Classroom) {
        viewModelScope.launch {
            repository.updateClassroom(classroom)
        }
    }

    fun deleteClassroom(classroom: Classroom) {
        viewModelScope.launch {
            repository.deleteClassroom(classroom)
            if (_selectedClassroom.value?.id == classroom.id) {
                val list = classrooms.value
                _selectedClassroom.value = if (list.isNotEmpty()) list.first() else null
            }
        }
    }

    // CRUD Student
    fun addStudent(classId: Int, name: String, gender: String, rollNumber: Int) {
        viewModelScope.launch {
            repository.insertStudent(
                Student(
                    classId = classId,
                    name = name,
                    gender = gender,
                    rollNumber = rollNumber
                )
            )
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    // Helper: Current Date String YYYY-MM-DD
    fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Calendar.getInstance().time)
    }

    // Convert Date format into Khmer textual representation
    fun convertToKhmerDate(dateString: String): String {
        try {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdfInput.parse(dateString) ?: return dateString
            val cal = Calendar.getInstance()
            cal.time = date

            val daysOfWeekKhmer = listOf(
                "អាទិត្យ", "ច័ន្ទ", "អង្គារ", "ពុធ", "ព្រហស្បតិ៍", "សុក្រ", "សៅរ៍"
            )
            val monthsKhmer = listOf(
                "មករា", "កុម្ភៈ", "មីនា", "មេសា", "ឧសភា", "មិថុនា",
                "កក្កដា", "សីហា", "កញ្ញា", "តុលា", "វិច្ឆិកា", "ធ្នូ"
            )

            val khmerDigits = listOf("០", "១", "២", "៣", "៤", "៥", "៦", "៧", "៨", "៩")
            
            fun toKhmerNumber(number: Int): String {
                return number.toString().map { char ->
                    if (char.isDigit()) {
                        khmerDigits[char.toString().toInt()]
                    } else {
                        char.toString()
                    }
                }.joinToString("")
            }

            val dayOfWeek = daysOfWeekKhmer[cal.get(Calendar.DAY_OF_WEEK) - 1]
            val dayOfMonth = toKhmerNumber(cal.get(Calendar.DAY_OF_MONTH))
            val month = monthsKhmer[cal.get(Calendar.MONTH)]
            val year = toKhmerNumber(cal.get(Calendar.YEAR))

            return "ថ្ងៃ$dayOfWeek ទី$dayOfMonth ខែ$month ឆ្នាំ$year"
        } catch (e: Exception) {
            return dateString
        }
    }
}
