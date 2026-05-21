package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.Classroom
import com.example.data.model.Student
import com.example.ui.viewmodel.AttendanceViewModel
import java.util.Calendar

@Composable
fun ReportScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val classrooms by viewModel.classrooms.collectAsState()
    val activeClassroom by viewModel.selectedClassroom.collectAsState()
    val studentsInClass by viewModel.studentsInSelectedClass.collectAsState()
    val activeDate by viewModel.attendanceDate.collectAsState()
    val allRecords by viewModel.allAttendanceRecords.collectAsState()

    // Screen-level state
    var selectedReportTab by remember { mutableStateOf(0) } // 0 = Daily view, 1 = Cumulative view

    // List of records for the active class on the active date
    val matchedRecords = allRecords.filter { it.classId == activeClassroom?.id && it.date == activeDate }
    val recordMap = matchedRecords.associateBy { it.studentId }

    // Setup DatePickerDialog
    val calendar = Calendar.getInstance()
    val cYear = calendar.get(Calendar.YEAR)
    val cMonth = calendar.get(Calendar.MONTH)
    val cDay = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, sYear, sMonth, sDay ->
            val formattedMonth = String.format("%02d", sMonth + 1)
            val formattedDay = String.format("%02d", sDay)
            viewModel.updateAttendanceDate("$sYear-$formattedMonth-$formattedDay")
        },
        cYear,
        cMonth,
        cDay
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("report_root")
    ) {
        // TOP Header
        Text(
            text = "របាយការណ៍លម្អិត",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "ពិនិត្យមើលវត្តមានប្រចាំថ្ងៃ និងស្ថិតិសរុបសិស្ស",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // TAB NAVIGATION (0 = Daily checklist, 1 = Cumulative total)
        ScrollableTabRow(
            selectedTabIndex = selectedReportTab,
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedReportTab == 0,
                onClick = { selectedReportTab = 0 },
                text = { Text("វត្តមានប្រចាំថ្ងៃ", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.History, contentDescription = null) }
            )
            Tab(
                selected = selectedReportTab == 1,
                onClick = { selectedReportTab = 1 },
                text = { Text("ស្ថិតិសរុបប្រចាំខែ", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Assessment, contentDescription = null) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CLASS SELECTOR ROW
        Text(
            text = "ជ្រើសរើសថ្នាក់រៀន៖",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (classrooms.isEmpty()) {
            Text("មិនទាន់មានថ្នាក់រៀននៅឡើយទេ។", color = Color.Red, fontSize = 14.sp)
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(classrooms) { classroom ->
                    val isSelected = activeClassroom?.id == classroom.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectClassroom(classroom) },
                        label = { Text(classroom.name, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeClassroom == null) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("សូមជ្រើសរើសថ្នាក់រៀនជាមុនសិន។")
            }
        } else if (selectedReportTab == 0) {
            // TAB 0: DAILY DETAILS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ថ្ងៃខែឆ្នាំជ្រើសរើស៖",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = viewModel.convertToKhmerDate(activeDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.clickable { datePickerDialog.show() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                        Text(activeDate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats breakdown
            val total = studentsInClass.size
            val excuseCount = matchedRecords.count { it.status == "E" }
            val absentCount = matchedRecords.count { it.status == "A" }
            val presentCount = total - excuseCount - absentCount
            val checked = matchedRecords.size

            val isDark = isSystemInDarkTheme()
            val themeGreen = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
            val themeAmber = if (isDark) Color(0xFFFFD54F) else Color(0xFFF57F17)
            val themeRed = if (isDark) Color(0xFFE57373) else Color(0xFFC62828)

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ReportStatsIndicator("វត្តមាន", "$presentCount", themeGreen)
                    ReportStatsIndicator("ច្បាប់", "$excuseCount", themeAmber)
                    ReportStatsIndicator("អត់ច្បាប់", "$absentCount", themeRed)
                    ReportStatsIndicator("អត្រាមកសាលា", "${if (total > 0) (presentCount * 100 / total) else 0}%", MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (checked == 0) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(44.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "មិនទាន់មានការស្រង់វត្តមានសិស្សនៅថ្ងៃនេះទេ",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "សូមទៅកាន់ទំព័រ \"ស្រង់វត្តមាន\" ដើម្បីយកវត្តមានថ្នាក់នេះ។",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val absentOrExcusedStudents = studentsInClass.filter { student ->
                    val record = recordMap[student.id]
                    record?.status == "A" || record?.status == "E"
                }

                if (absentOrExcusedStudents.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(44.dp), tint = themeGreen)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "គ្មានសិស្សអវត្តមានទេ (វត្តមានគ្រប់គ្រាន់)",
                                fontWeight = FontWeight.Bold,
                                color = themeGreen
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(absentOrExcusedStudents) { student ->
                            val record = recordMap[student.id]
                            DailyReportRow(student = student, record = record)
                        }
                    }
                }
            }
        } else {
            // TAB 1: CUMULATIVE STATS
            Text(
                text = "ស្ថិតិវត្តមានសរុប (គិតត្រឹមកាលបរិច្ឆេទសរុប)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (studentsInClass.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("គ្មានទិន្នន័យសិស្សទេ។")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(studentsInClass) { student ->
                        // Get all historic records for this student
                        val studentRecords = allRecords.filter { it.studentId == student.id }
                        val presents = studentRecords.count { it.status == "P" }
                        val excuses = studentRecords.count { it.status == "E" }
                        val absents = studentRecords.count { it.status == "A" }
                        val totalRecordedDays = studentRecords.size

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text(student.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "លេខរៀង៖ ${student.rollNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val isDarkEnv = isSystemInDarkTheme()
                                    val greenCol = if (isDarkEnv) Color(0xFF81C784) else Color(0xFF2E7D32)
                                    val yellowCol = if (isDarkEnv) Color(0xFFFFD54F) else Color(0xFFFBC02D)
                                    val redCol = if (isDarkEnv) Color(0xFFE57373) else Color(0xFFD32F2F)

                                    // Present count pill
                                    MiniStatView(count = "$presents", label = "វត្តមាន", color = greenCol)
                                    // Excuse count pill
                                    MiniStatView(count = "$excuses", label = "ច្បាប់", color = yellowCol)
                                    // Absent count pill
                                    MiniStatView(count = "$absents", label = "អត់", color = redCol)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportStatsIndicator(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun MiniStatView(
    count: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(36.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(count, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DailyReportRow(
    student: Student,
    record: AttendanceRecord?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${student.rollNumber}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(student.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "លេខរៀង៖ ${student.rollNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val isDark = isSystemInDarkTheme()
            val statusText: String
            val statusColor: Color
            when (record?.status) {
                "P" -> {
                    statusText = "វត្តមាន"
                    statusColor = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                }
                "E" -> {
                    statusText = "មានច្បាប់"
                    statusColor = if (isDark) Color(0xFFFFD54F) else Color(0xFFFBC02D)
                }
                "A" -> {
                    statusText = "អត់ច្បាប់"
                    statusColor = if (isDark) Color(0xFFE57373) else Color(0xFFD32F2F)
                }
                else -> {
                    statusText = "មិនទាន់ស្រង់"
                    statusColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
