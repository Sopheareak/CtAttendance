package com.example.ui.screens

import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Classroom
import com.example.data.model.Student
import com.example.ui.viewmodel.AttendanceViewModel
import java.util.Calendar

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val classrooms by viewModel.classrooms.collectAsState()
    val selectedClass by viewModel.selectedClassroom.collectAsState()
    val students by viewModel.studentsInSelectedClass.collectAsState()
    val activeDate by viewModel.attendanceDate.collectAsState()
    val draftRecords by viewModel.draftAttendance.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }
    var exportTimeRange by remember { mutableStateOf("០៧:០០ - ០៨:៣០") }
    var exportSubject by remember { mutableStateOf("គណិតវិទ្យា") }
    val subjectsList = remember {
        listOf(
            "សរសេរតាមអាន", "តែងសេចក្តី", "ល្បឿនអំណាន", "ភាសាខ្មែរ", "គណិតវិទ្យា",
            "រូបវិទ្យា", "គីមីវិទ្យា", "ជីវវិទ្យា", "ប្រវត្តិវិទ្យា", "ព័ត៌មានវិទ្យា",
            "សីល-ពលរដ្ឋ", "ផែនដីវិទ្យា", "ភូមិវិទ្យា", "គេហវិទ្យា", "អប់រំកាយ",
            "ភាសាចិន", "បំណិន", "សេដ្ឋកិច្ច", "សិល្បៈ", "កសិកម្ម", "ភាសាបរទេស"
        )
    }

    // Setup DatePickerDialog
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, sYear, sMonth, sDay ->
            val formattedMonth = String.format("%02d", sMonth + 1)
            val formattedDay = String.format("%02d", sDay)
            viewModel.updateAttendanceDate("$sYear-$formattedMonth-$formattedDay")
        },
        currentYear,
        currentMonth,
        currentDay
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("attendance_root")
    ) {
        // TOP Header Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ស្រង់វត្តមានសិស្ស",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Date Picker trigger button
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.clickable { datePickerDialog.show() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Select Date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = activeDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = viewModel.convertToKhmerDate(activeDate),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        // CLASSROOM SELECTOR ROW
        Text(
            text = "ជ្រើសរើសថ្នាក់រៀន៖",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (classrooms.isEmpty()) {
            Text(
                text = "មិនទាន់មានថ្នាក់រៀននៅឡើយ។ សូមទៅកាន់ទំព័រ \"គ្រប់គ្រង\" ដើម្បីបង្កើតថ្នាក់រៀន។",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(classrooms) { classroom ->
                    val isSelected = selectedClass?.id == classroom.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectClassroom(classroom) },
                        label = { Text(classroom.name, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("classroom_chip_${classroom.id}")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ACTIVE SUMMARY OF CURRENT WORK
        if (selectedClass != null && students.isNotEmpty()) {
            val total = students.size
            val excuseNum = draftRecords.values.count { it.status == "E" }
            val absentNum = draftRecords.values.count { it.status == "A" }
            val presentNum = total - excuseNum - absentNum

            val isDark = isSystemInDarkTheme()
            val themeGreen = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
            val themeAmber = if (isDark) Color(0xFFFFD54F) else Color(0xFFF57F17)
            val themeRed = if (isDark) Color(0xFFE57373) else Color(0xFFC62828)

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("សរុប", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("វត្តមាន", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$presentNum", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = themeGreen)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ច្បាប់", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$excuseNum", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = themeAmber)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("អត់ច្បាប់", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$absentNum", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = themeRed)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // STUDENT LIST WITH P/E/A ACTIONS
        if (selectedClass == null) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "សូមជ្រើសរើសថ្នាក់រៀនជាមុនសិន។",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (students.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No Students",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "មិនមានសិស្សនៅក្នុងថ្នាក់នេះទេ។",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "សូមទៅកាន់ទំព័រ \"គ្រប់គ្រង\" ដើម្បីបន្ថែមសិស្សទៅកាន់ថ្នាក់នេះ។",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            // Student attendance grid list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(students) { student ->
                    val record = draftRecords[student.id]
                    val currentStatus = record?.status

                    StudentAttendanceCell(
                        student = student,
                        currentStatus = currentStatus,
                        onStatusChanged = { status ->
                            viewModel.markAttendance(student.id, status)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (selectedClass != null && students.isNotEmpty()) {
                        // EXPORT TO PRINCIPAL ACTION BUTTON
                        Button(
                            onClick = {
                                showExportDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("export_to_principal_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "Export to Principal",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("របាយការណ៍ផ្ញើទៅនាយក (Export/Share Card)", fontWeight = FontWeight.Bold)
                        }
                    }

                    // SAVE ACTIONS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.clearDraftAttendance()
                                Toast.makeText(context, "សម្អាតសេចក្តីព្រាងរួចរាល់", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = "Clear draft")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("សម្អាត (Clear)")
                        }

                        Button(
                            onClick = {
                                viewModel.submitAttendance()
                                Toast.makeText(context, "រក្សាទុកវត្តមានថ្នាក់ ${selectedClass?.name} ជោគជ័យ!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("save_attendance_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("រក្សាទុក (Save)")
                        }
                    }
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }

    // =========================================================
    // EXPORT REPORT FOR PRINCIPAL (នាយក) DIALOG
    // =========================================================
    if (showExportDialog && selectedClass != null) {
        val excuseCount = students.count { draftRecords[it.id]?.status == "E" }
        val absentCount = students.count { draftRecords[it.id]?.status == "A" }
        val presentCount = students.size - excuseCount - absentCount
        val excusedList = students.filter { draftRecords[it.id]?.status == "E" }
        val absentList = students.filter { draftRecords[it.id]?.status == "A" }
        val khmerDate = viewModel.convertToKhmerDate(activeDate)

        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    text = "របាយការណ៍ផ្ញើជូនលោកនាយកសាលា",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "សូមបំពេញកាលបរិច្ឆេទ ម៉ោងសិក្សា និងជ្រើសរើសមុខវិជ្ជា ដើម្បីចងក្រងទៅជាកាតរបាយការណ៍ផ្លូវការ៖",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // DATE FIELD (Clickable to open Datepicker)
                    item {
                        OutlinedTextField(
                            value = activeDate,
                            onValueChange = {},
                            label = { Text("កាលបរិច្ឆេទ (Date)") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = "Choose Date")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() }
                        )
                    }

                    // LEARNING HOURS FIELD
                    item {
                        OutlinedTextField(
                            value = exportTimeRange,
                            onValueChange = { exportTimeRange = it },
                            label = { Text("ម៉ោងសិក្សា (Study Hours)") },
                            placeholder = { Text("០៧:០០ - ០៨:៣០") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Suggested Time chips
                        Text("សំណើម៉ោងសិក្សា៖", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            val timeSlots = listOf("០៧:០០ - ០៨:៣០", "០៨:៤៥ - ១០:១៥", "១៣:០០ - ១៤:៣០", "១៤:៤៥ - ១៦:១៥")
                            items(timeSlots) { slot ->
                                FilterChip(
                                    selected = exportTimeRange == slot,
                                    onClick = { exportTimeRange = slot },
                                    label = { Text(slot, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // SUBJECT NAME SELECTOR CHIPS (21 Khmer Subjects)
                    item {
                        Text("ឈ្មោះមុខវិជ្ជា៖", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                val chunks = subjectsList.chunked(4)
                                chunks.forEach { chunk ->
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                    ) {
                                        items(chunk) { subject ->
                                            val isSel = exportSubject == subject
                                            FilterChip(
                                                selected = isSel,
                                                onClick = { exportSubject = subject },
                                                label = { Text(subject, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // =============================================
                    // VISUAL PREVIEW REPORT CARD (Official Style)
                    // =============================================
                    item {
                        Text(
                            text = "ទិដ្ឋភាពកាតរួមសម្រាប់ Export (Preview):",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .clip(RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.DarkGray)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Khmer traditional heading headers
                                Text(
                                    text = "ព្រះរាជាណាចក្រកម្ពុជា",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "ជាតិ សាសនា ព្រះមហាក្សត្រ",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                
                                // Decorative wave divider style
                                Text(
                                    text = "✍✍✍", 
                                    fontSize = 10.sp, 
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Column {
                                        Text("សាលាអនុវិទ្យាល័យឈើទាល", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        Text("ថ្នាក់រៀន៖ ${selectedClass?.name}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Document Title
                                Text(
                                    text = "របាយការណ៍វត្តមានសិស្សប្រចាំថ្ងៃ",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Informational Details Grid
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("• មុខវិជ្ជា៖ $exportSubject", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                                    Text("• ម៉ោងសិក្សា៖ $exportTimeRange", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                                    Text("• កាលបរិច្ឆេទ៖ $khmerDate", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                androidx.compose.material3.HorizontalDivider(color = Color.DarkGray, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Stats segment
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("សរុប៖ ${students.size} នាក់", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    Text("វត្តមាន៖ $presentCount នាក់", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                    Text("ច្បាប់ (E)៖ $excuseCount នាក់", fontSize = 11.sp, color = Color(0xFFF57F17), fontWeight = FontWeight.Bold)
                                    Text("អត់ច្បាប់ (A)៖ $absentCount នាក់", fontSize = 11.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Section with Student Absences
                                Text(
                                    text = "បញ្ជីឈ្មោះសិស្សអវត្តមានសរុប",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                if (excusedList.isEmpty() && absentList.isEmpty()) {
                                    Text(
                                        text = "-> គ្មានសិស្សអវត្តមានទេ (វត្តមានគ្រប់គ្រាន់)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (absentList.isNotEmpty()) {
                                            Text("● អវត្តមានអត់ច្បាប់ (A):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                            absentList.forEachIndexed { i, student ->
                                                Text(
                                                    text = "   ${i + 1}. លេខ ${student.rollNumber} - ${student.name}",
                                                    fontSize = 11.sp,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                        if (excusedList.isNotEmpty()) {
                                            Text("● អវត្តមានមានច្បាប់ (E):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                                            excusedList.forEachIndexed { i, student ->
                                                Text(
                                                    text = "   ${i + 1}. លេខ ${student.rollNumber} - ${student.name}",
                                                    fontSize = 11.sp,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "គ្រូទទួលបន្ទុកថ្នាក់ (ហត្ថលេខា)",
                                    fontSize = 10.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Thin,
                                    modifier = Modifier.align(Alignment.End)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // BUTTON TO SAVE IMAGE JPG
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val shareText = """
                            សូមគោរពអនុញ្ញាតជូនលោកនាយក អនុវិទ្យាល័យឈើទាល
    
                            *របាយការណ៍វត្តមានសិស្សប្រចាំថ្ងៃ*
                            • ថ្នាក់រៀន៖ ${selectedClass?.name ?: "N/A"}
                            • មុខវិជ្ជា៖ $exportSubject
                            • ម៉ោងសិក្សា៖ $exportTimeRange
                            • កាលបរិច្ឆេទ៖ $khmerDate ($activeDate)
    
                            *ស្ថិតិសរុប៖*
                            • សិស្សសរុប៖ ${students.size} នាក់
                            • វត្តមានជាក់ស្តែង៖ $presentCount នាក់
                            • អវត្តមានសរុប៖ ${excuseCount + absentCount} នាក់
                              - មានច្បាប់ (E)៖ $excuseCount នាក់
                              - អត់ច្បាប់ (A)៖ $absentCount នាក់
    
                            *បញ្ជីឈ្មោះសិស្សអវត្តមាន៖*
                            ${
                                if (excusedList.isEmpty() && absentList.isEmpty()) {
                                    "-> គ្មានសិស្សអវត្តមានទេ (សិស្សមានវត្តមានគ្រប់គ្រាន់)"
                                } else {
                                    var idx = 1
                                    val lines = mutableListOf<String>()
                                    if (absentList.isNotEmpty()) {
                                        lines.add(" [អត់ច្បាប់ - Absent]")
                                        absentList.forEach { 
                                            lines.add("  ${idx++}. ${it.name} (លេខរៀង៖ ${it.rollNumber})")
                                        }
                                    }
                                    if (excusedList.isNotEmpty()) {
                                        lines.add(" [មានច្បាប់ - Excused]")
                                        excusedList.forEach { 
                                            lines.add("  ${idx++}. ${it.name} (លេខរៀង៖ ${it.rollNumber})")
                                        }
                                    }
                                    lines.joinToString("\n")
                                }
                            }
    
                            សូមអរគុណ!
                            """.trimIndent()
                            val clip = ClipData.newPlainText("Attendance Report", shareText)
                            clipboard.setPrimaryClip(clip)
                            
                            // Call the high-quality real JPG exporter
                            saveReportAsJpg(
                                context = context,
                                classroomName = selectedClass?.name ?: "N/A",
                                subject = exportSubject,
                                timeRange = exportTimeRange,
                                date = activeDate,
                                khmerDate = khmerDate,
                                totalCount = students.size,
                                presentCount = presentCount,
                                excuseCount = excuseCount,
                                absentCount = absentCount,
                                excusedList = excusedList,
                                absentList = absentList
                            )
                            
                            showExportDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Image", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("រក្សាទុក JPG")
                    }

                    // SHARE TEXT DIRECTLY TO TELEGRAM OR SYSTEM
                    Button(
                        onClick = {
                            val shareText = """
                            សូមគោរពអនុញ្ញាតជូនលោកនាយក អនុវិទ្យាល័យឈើទាល
    
                            *របាយការណ៍វត្តមានសិស្សប្រចាំថ្ងៃ*
                            • ថ្នាក់រៀន៖ ${selectedClass?.name ?: "N/A"}
                            • មុខវិជ្ជា៖ $exportSubject
                            • ម៉ោងសិក្សា៖ $exportTimeRange
                            • កាលបរិច្ឆេទ៖ $khmerDate ($activeDate)
    
                            *ស្ថិតិសរុប៖*
                            • សិស្សសរុប៖ ${students.size} នាក់
                            • វត្តមានជាក់ស្តែង៖ $presentCount នាក់
                            • អវត្តមានសរុប៖ ${excuseCount + absentCount} នាក់
                              - មានច្បាប់ (E)៖ $excuseCount នាក់
                              - អត់ច្បាប់ (A)៖ $absentCount នាក់
    
                            *បញ្ជីឈ្មោះសិស្សអវត្តមាន៖*
                            ${
                                if (excusedList.isEmpty() && absentList.isEmpty()) {
                                    "-> គ្មានសិស្សអវត្តមានទេ (សិស្សមានវត្តមានគ្រប់គ្រាន់)"
                                } else {
                                    var idx = 1
                                    val lines = mutableListOf<String>()
                                    if (absentList.isNotEmpty()) {
                                        lines.add(" [អត់ច្បាប់ - Absent]")
                                        absentList.forEach { 
                                            lines.add("  ${idx++}. ${it.name} (លេខរៀង៖ ${it.rollNumber})")
                                        }
                                    }
                                    if (excusedList.isNotEmpty()) {
                                        lines.add(" [មានច្បាប់ - Excused]")
                                        excusedList.forEach { 
                                            lines.add("  ${idx++}. ${it.name} (លេខរៀង៖ ${it.rollNumber})")
                                        }
                                    }
                                    lines.joinToString("\n")
                                }
                            }
    
                            សូមអរគុណ!
                            """.trimIndent()
                            
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "ចែករំលែករបាយការណ៍ទៅកាន់លោកនាយក")
                            context.startActivity(shareIntent)
                            showExportDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ចែករំលែក (Share)")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("បោះបង់")
                }
            }
        )
    }
}

@Composable
fun StudentAttendanceCell(
    student: Student,
    currentStatus: String?,
    onStatusChanged: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val greenColor = if (isDark) Color(0xFF388E3C) else Color(0xFF2E7D32)
    val yellowColor = if (isDark) Color(0xFFFBC02D) else Color(0xFFF57F17)
    val yellowText = if (isDark) Color.Black else Color.White
    val redColor = if (isDark) Color(0xFFD32F2F) else Color(0xFFC62828)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1.2f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${student.rollNumber}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "លេខរៀង៖ ${student.rollNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Attendance interactive controls (P, E, A)
            Row(
                modifier = Modifier.weight(1.1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Present button P
                AttendanceButton(
                    label = "វ", // វត្តមាន
                    activeColor = greenColor,
                    tintActive = Color.White,
                    isActive = currentStatus == "P" || currentStatus == null,
                    onClick = { onStatusChanged("P") },
                    modifier = Modifier.weight(1f).testTag("att_p_btn_${student.id}")
                )

                // Excused button E
                AttendanceButton(
                    label = "ច", // ច្បាប់
                    activeColor = yellowColor,
                    tintActive = yellowText,
                    isActive = currentStatus == "E",
                    onClick = { onStatusChanged("E") },
                    modifier = Modifier.weight(1f).testTag("att_e_btn_${student.id}")
                )

                // Absent button A
                AttendanceButton(
                    label = "អ", // អត់ច្បាប់
                    activeColor = redColor,
                    tintActive = Color.White,
                    isActive = currentStatus == "A",
                    onClick = { onStatusChanged("A") },
                    modifier = Modifier.weight(1f).testTag("att_a_btn_${student.id}")
                )
            }
        }
    }
}

@Composable
fun AttendanceButton(
    label: String,
    activeColor: Color,
    tintActive: Color,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val inactiveBg = if (isDark) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    }
    val inactiveTextColor = if (isDark) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) activeColor else inactiveBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            color = if (isActive) tintActive else inactiveTextColor
        )
    }
}

// ==========================================
// HIGH-QUALITY REAL JPG EXPORTER IN KHMER STYLE
// ==========================================
fun saveReportAsJpg(
    context: android.content.Context,
    classroomName: String,
    subject: String,
    timeRange: String,
    date: String,
    khmerDate: String,
    totalCount: Int,
    presentCount: Int,
    excuseCount: Int,
    absentCount: Int,
    excusedList: List<com.example.data.model.Student>,
    absentList: List<com.example.data.model.Student>
) {
    try {
        val width = 1000
        
        // Calculate dynamic height based on list sizes to fit everything beautifully!
        val baseHeight = 780
        val absentHeight = if (absentList.isNotEmpty()) 50 + (absentList.size * 42) else 0
        val excusedHeight = if (excusedList.isNotEmpty()) 50 + (excusedList.size * 42) else 0
        val totalHeight = maxOf(960, baseHeight + absentHeight + excusedHeight)
        
        val bitmap = android.graphics.Bitmap.createBitmap(width, totalHeight, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        
        // 1. Draw solid background
        val bgPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            style = android.graphics.Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), totalHeight.toFloat(), bgPaint)
        
        // 2. Draw thick outer borders
        val borderPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#374151") // Slate dark color
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 12f
        }
        canvas.drawRect(20f, 20f, (width - 20).toFloat(), (totalHeight - 20).toFloat(), borderPaint)
        
        // Dynamic decorative double border (inner thin border)
        val innerBorderPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#9CA3AF") // light slate color
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRect(32f, 32f, (width - 32).toFloat(), (totalHeight - 32).toFloat(), innerBorderPaint)
        
        // 3. Initialize dynamic text paints
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            isAntiAlias = true
            textSize = 28f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
        }
        
        val headerPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            isAntiAlias = true
            textSize = 34f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }

        val titlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#1E3A8A") // Dark Blue accent
            isAntiAlias = true
            textSize = 38f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
        
        var currentY = 80f
        
        // Draw Kingdom header (Centered)
        val kingdomTitle = "ព្រះរាជាណាចក្រកម្ពុជា"
        val kingdomWidth = headerPaint.measureText(kingdomTitle)
        canvas.drawText(kingdomTitle, (width - kingdomWidth) / 2f, currentY, headerPaint)
        
        currentY += 45f
        val mottoTitle = "ជាតិ សាសនា ព្រះមហាក្សត្រ"
        val mottoWidth = textPaint.measureText(mottoTitle)
        canvas.drawText(mottoTitle, (width - mottoWidth) / 2f, currentY, textPaint)
        
        // Draw waves flourish
        currentY += 40f
        val flourish = "✍✍✍"
        val flourishWidth = textPaint.measureText(flourish)
        canvas.drawText(flourish, (width - flourishWidth) / 2f, currentY, textPaint)
        
        // Draw School & Classroom details on left side
        currentY += 50f
        textPaint.textSize = 26f
        textPaint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("សាលាអនុវិទ្យាល័យឈើទាល", 60f, currentY, textPaint)
        
        currentY += 40f
        canvas.drawText("ថ្នាក់រៀន៖ $classroomName", 60f, currentY, textPaint)
        
        // Draw main report header banner with beautiful filled shape background!
        currentY += 70f
        val bannerHeight = 84f
        val bannerY = currentY - 55f
        val bannerPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#F3F4F6") // smooth gray background
            style = android.graphics.Paint.Style.FILL
        }
        val bannerRect = android.graphics.RectF(50f, bannerY, (width - 50).toFloat(), bannerY + bannerHeight)
        canvas.drawRoundRect(bannerRect, 10f, 10f, bannerPaint)
        
        // Banner outline
        val bannerOutlinePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#D1D5DB")
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(bannerRect, 10f, 10f, bannerOutlinePaint)
        
        // Draw main banner title text
        val docTitle = "របាយការណ៍វត្តមានសិស្សប្រចាំថ្ងៃ"
        val titleWidth = titlePaint.measureText(docTitle)
        canvas.drawText(docTitle, (width - titleWidth) / 2f, bannerY + 54f, titlePaint)
        
        // Details list (Subject, Hours, Date)
        currentY += 60f
        textPaint.textSize = 28f
        textPaint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
        
        canvas.drawText("• មុខវិជ្ជា៖ $subject", 70f, currentY, textPaint)
        currentY += 45f
        canvas.drawText("• ម៉ោងសិក្សា៖ $timeRange", 70f, currentY, textPaint)
        currentY += 45f
        canvas.drawText("• កាលបរិច្ឆេទ៖ $khmerDate ($date)", 70f, currentY, textPaint)
        
        // Thin gray line divider
        currentY += 40f
        val dividerPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#9CA3AF")
            strokeWidth = 2f
        }
        canvas.drawLine(50f, currentY, (width - 50).toFloat(), currentY, dividerPaint)
        
        // Stats segment row (spacious)
        currentY += 50f
        val statsTextSize = 27f
        val statsPaint = android.graphics.Paint().apply {
            textSize = statsTextSize
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
        
        // Total stats (black)
        statsPaint.color = android.graphics.Color.BLACK
        canvas.drawText("សរុប៖ $totalCount នាក់", 70f, currentY, statsPaint)
        
        // Present stats (green)
        statsPaint.color = android.graphics.Color.parseColor("#15803D")
        canvas.drawText("វត្តមាន៖ $presentCount នាក់", 310f, currentY, statsPaint)
        
        // Excused stats (Orange/Amber)
        statsPaint.color = android.graphics.Color.parseColor("#B45309")
        canvas.drawText("ច្បាប់ (E)៖ $excuseCount នាក់", 540f, currentY, statsPaint)
        
        // Absent stats (Red)
        statsPaint.color = android.graphics.Color.parseColor("#B91C1C")
        canvas.drawText("អត់ច្បាប់ (A)៖ $absentCount នាក់", 760f, currentY, statsPaint)
        
        // Solid black line below stats
        currentY += 35f
        dividerPaint.color = android.graphics.Color.BLACK
        dividerPaint.strokeWidth = 3f
        canvas.drawLine(50f, currentY, (width - 50).toFloat(), currentY, dividerPaint)
        
        // Section Title: Student Absences
        currentY += 50f
        val sectionTitlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 28f
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
        canvas.drawText("បញ្ជីឈ្មោះសិស្សអវត្តមានសរុប៖", 70f, currentY, sectionTitlePaint)
        
        currentY += 45f
        if (excusedList.isEmpty() && absentList.isEmpty()) {
            val allPresentPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#15803D")
                textSize = 26f
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }
            canvas.drawText("-> គ្មានសិស្សអវត្តមានទេ (វត្តមានគ្រប់គ្រាន់)", 100f, currentY, allPresentPaint)
        } else {
            val absentItemPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 26f
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
            }
            
            var index = 1
            
            // Draw list of unexcused absences
            if (absentList.isNotEmpty()) {
                val groupPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#B91C1C")
                    textSize = 26f
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                }
                canvas.drawText("● អវត្តមានអត់ច្បាប់ (A):", 90f, currentY, groupPaint)
                currentY += 40f
                
                absentList.forEach { student ->
                    val txt = "   $index. លេខ ${student.rollNumber} - ${student.name}"
                    canvas.drawText(txt, 110f, currentY, absentItemPaint)
                    currentY += 42f
                    index++
                }
            }
            
            // Draw list of excused absences
            if (excusedList.isNotEmpty()) {
                val groupPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#B45309")
                    textSize = 26f
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                }
                canvas.drawText("● អវត្តមានមានច្បាប់ (E):", 90f, currentY, groupPaint)
                currentY += 40f
                
                excusedList.forEach { student ->
                    val txt = "   $index. លេខ ${student.rollNumber} - ${student.name}"
                    canvas.drawText(txt, 110f, currentY, absentItemPaint)
                    currentY += 42f
                    index++
                }
            }
        }
        
        // Footer signature
        val footerPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 24f
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.ITALIC)
        }
        val sigLine = "គ្រូទទួលបន្ទុកថ្នាក់ (ហត្ថលេខា)"
        val sigWidth = footerPaint.measureText(sigLine)
        canvas.drawText(sigLine, width - sigWidth - 70f, totalHeight - 110f, footerPaint)
        
        // Save Bitmap to MediaStore
        val resolver = context.contentResolver
        val filename = "ATTENDANCE_${classroomName}_${date.replace("-", "")}.jpg"
        
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AttendanceReports")
                put(android.provider.MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        
        val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri).use { stream ->
                if (stream != null) {
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 95, stream)
                }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            android.widget.Toast.makeText(context, "បានរក្សាទុកកាតរូបភាពក្នុង Album/Gallery ជោគជ័យ! ($filename)", android.widget.Toast.LENGTH_LONG).show()
        } else {
            android.widget.Toast.makeText(context, "មានបញ្ហានៅពេលបង្កើតឯកសាររូបភាព", android.widget.Toast.LENGTH_SHORT).show()
        }
        
    } catch (e: Exception) {
        e.printStackTrace()
        android.widget.Toast.makeText(context, "កំហុស៖ " + e.localizedMessage, android.widget.Toast.LENGTH_LONG).show()
    }
}
