package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Classroom
import com.example.data.model.Student
import com.example.ui.viewmodel.AttendanceViewModel

@Composable
fun ManageScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val classrooms by viewModel.classrooms.collectAsState()
    val activeClassroom by viewModel.selectedClassroom.collectAsState()
    val studentsInClass by viewModel.studentsInSelectedClass.collectAsState()

    // Dialog trigger states
    var showAddClassDialog by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showAddBulkStudentDialog by remember { mutableStateOf(false) }
    var showDeleteClassConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteStudentConfirmDialog by remember { mutableStateOf<Student?>(null) }

    // Input values states
    var newClassName by remember { mutableStateOf("") }
    var newStudentName by remember { mutableStateOf("") }
    var newStudentGender by remember { mutableStateOf("ប្រុស") }
    var newStudentRollNumber by remember { mutableStateOf("") }
    var bulkStudentsText by remember { mutableStateOf("") }
    var bulkStudentsGender by remember { mutableStateOf("ប្រុស") }

    // State to toggle between Managing Classrooms or Students list internally
    var selectedManageTab by remember { mutableStateOf(0) } // 0 = Classes, 1 = Students in current class

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("manage_root")
    ) {
        Text(
            text = "គ្រប់គ្រងប្រព័ន្ធ",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "គ្រប់គ្រងព័ត៌មានថ្នាក់រៀន និងបញ្ជីឈ្មោះសិស្ស",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // UPPER SEGMENT BANNER FOR TABS
        ScrollableTabRow(
            selectedTabIndex = selectedManageTab,
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedManageTab == 0,
                onClick = { selectedManageTab = 0 },
                text = { Text("ថ្នាក់រៀន (${classrooms.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Class, contentDescription = null) }
            )
            Tab(
                selected = selectedManageTab == 1,
                onClick = { selectedManageTab = 1 },
                text = {
                    Text(
                        text = "សិស្សថ្នាក់៖ ${activeClassroom?.name ?: "គ្មានថ្នាក់"}",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                icon = { Icon(Icons.Default.Group, contentDescription = null) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedManageTab == 0) {
            // TAB 0: CLASSROOMS CRUD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "បញ្ជីថ្នាក់រៀន",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { showAddClassDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Class")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("បង្កើតថ្នាក់ថ្មី")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (classrooms.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("មិនទាន់មានថ្នាក់រៀននៅឡើយទេ។ សូមចុច \"បង្កើតថ្នាក់ថ្មី\" ដើម្បីបន្ថែម។", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(classrooms) { classroom ->
                        val isSelected = activeClassroom?.id == classroom.id
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectClassroom(classroom) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Class,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = classroom.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isSelected) "ថ្នាក់កំពុងជ្រើសរើសសម្រាប់ស្រង់វត្តមាន" else "ចុចដើម្បីជ្រើសរើសថ្នាក់",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            viewModel.selectClassroom(classroom)
                                            showDeleteClassConfirmDialog = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Class",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TAB 1: STUDENTS LIST CRUD IN CURRENT CLASSROOM
            if (activeClassroom == null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("សូមជ្រើសរើសថ្នាក់រៀននៅក្នុងកាត \"ថ្នាក់រៀន\" ជាមុនសិន!")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "បញ្ជីឈ្មោះសិស្សថ្នាក់ ${activeClassroom?.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "សរុប៖ ${studentsInClass.size} នាក់",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                newStudentRollNumber = "${studentsInClass.size + 1}"
                                showAddStudentDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Add Student", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("បន្ថែមម្នាក់ៗ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                showAddBulkStudentDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Group, contentDescription = "Add Bulk Students", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("បន្ថែមឈ្មោះច្រើន", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (studentsInClass.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("គ្មានសិស្សនៅក្នុងថ្នាក់នេះទេ។ សូមចុច \"បន្ថែមម្នាក់ៗ\" ឬ \"បន្ថែមឈ្មោះច្រើន\" ដើម្បីបញ្ចូលបញ្ជីឈ្មោះសិស្ស។", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(studentsInClass) { student ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(10.dp),
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${student.rollNumber}",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.secondary,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = student.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "លេខរៀង៖ ${student.rollNumber}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { showDeleteStudentConfirmDialog = student }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete student",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.82f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // ADD CLASS DIALOG
    // ==========================================
    if (showAddClassDialog) {
        AlertDialog(
            onDismissRequest = { showAddClassDialog = false },
            title = { Text("បង្កើតថ្នាក់រៀនថ្មី", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("សូមបញ្ចូលឈ្មោះថ្នាក់រៀន (ឧទាហរណ៍៖ ថ្នាក់ទី ៧ ក ឬ Grade 7A)", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newClassName,
                        onValueChange = { newClassName = it },
                        label = { Text("ឈ្មោះថ្នាក់") },
                        modifier = Modifier.fillMaxWidth().testTag("new_classroom_name_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newClassName.isNotBlank()) {
                            viewModel.testAddClassroom(newClassName)
                            Toast.makeText(context, "បង្កើតថ្នាក់ $newClassName រួចរាល់", Toast.LENGTH_SHORT).show()
                            newClassName = ""
                            showAddClassDialog = false
                        } else {
                            Toast.makeText(context, "ឈ្មោះថ្នាក់មិនអាចទទេបានទេ", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("submit_classroom_dialog_btn")
                ) {
                    Text("បង្កើត")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddClassDialog = false }) {
                    Text("បោះបង់")
                }
            }
        )
    }

    // ==========================================
    // ADD STUDENT DIALOG
    // ==========================================
    if (showAddStudentDialog && activeClassroom != null) {
        AlertDialog(
            onDismissRequest = { showAddStudentDialog = false },
            title = { Text("បញ្ចូលឈ្មោះសិស្សថ្មី", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "ថ្នាក់រៀន៖ ${activeClassroom?.name}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = newStudentName,
                        onValueChange = { newStudentName = it },
                        label = { Text("ឈ្មោះសិស្ស (Khmer Name)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newStudentRollNumber,
                        onValueChange = { newStudentRollNumber = it },
                        label = { Text("លេខរៀង") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rollNum = newStudentRollNumber.toIntOrNull() ?: (studentsInClass.size + 1)
                        if (newStudentName.isNotBlank()) {
                            viewModel.addStudent(
                                classId = activeClassroom!!.id,
                                name = newStudentName,
                                gender = newStudentGender,
                                rollNumber = rollNum
                            )
                            Toast.makeText(context, "បានបញ្ចូល $newStudentName ជោគជ័យ", Toast.LENGTH_SHORT).show()
                            newStudentName = ""
                            showAddStudentDialog = false
                        } else {
                            Toast.makeText(context, "ឈ្មោះសិស្សមិនអាចទទេបានទេ", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("រក្សាទុក")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStudentDialog = false }) {
                    Text("បោះបង់")
                }
            }
        )
    }

    // ==========================================
    // DELETE CLASS CONFIRMATION
    // ==========================================
    if (showDeleteClassConfirmDialog && activeClassroom != null) {
        AlertDialog(
            onDismissRequest = { showDeleteClassConfirmDialog = false },
            title = { Text("លុបថ្នាក់រៀន?", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) },
            text = {
                Text("តើអ្នកពិតជាចង់លុបថ្នាក់ \"${activeClassroom?.name}\" មែនទេ? រាល់គណនីឈ្មោះសិស្ស និងកំណត់ត្រាវត្តមានទាំងអស់នៅក្នុងថ្នាក់នេះនឹងត្រូវលុបចោលទាំងស្រុង។")
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        viewModel.deleteClassroom(activeClassroom!!)
                        Toast.makeText(context, "លុបថ្នាក់រួចរាល់", Toast.LENGTH_SHORT).show()
                        showDeleteClassConfirmDialog = false
                    }
                ) {
                    Text("លុបថ្នាក់រៀន")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteClassConfirmDialog = false }) {
                    Text("បោះបង់")
                }
            }
        )
    }

    // ==========================================
    // DELETE STUDENT CONFIRMATION
    // ==========================================
    if (showDeleteStudentConfirmDialog != null) {
        val student = showDeleteStudentConfirmDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteStudentConfirmDialog = null },
            title = { Text("លុបសិស្សម្នាក់នេះ?", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) },
            text = {
                Text("តើអ្នកពិតជាចង់លុបសិស្សឈ្មោះ \"${student.name}\" ចេញពីបញ្ជីថ្នាក់រៀនមែនទេ?")
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    onClick = {
                        viewModel.deleteStudent(student)
                        Toast.makeText(context, "បានលុបសិស្សរួចរាល់", Toast.LENGTH_SHORT).show()
                        showDeleteStudentConfirmDialog = null
                    }
                ) {
                    Text("លុបសិស្ស")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteStudentConfirmDialog = null }) {
                    Text("បោះបង់")
                }
            }
        )
    }

    // ==========================================
    // ADD BULK STUDENTS DIALOG
    // ==========================================
    if (showAddBulkStudentDialog && activeClassroom != null) {
        AlertDialog(
            onDismissRequest = { showAddBulkStudentDialog = false },
            title = { Text("បន្ថែមសិស្សជាច្រើននាក់រហ័ស", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "ថ្នាក់រៀន៖ ${activeClassroom?.name}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "សូមវាយ ឬចម្លង (Copy/Paste) ឈ្មោះសិស្សចូលក្នុងប្រអប់ខាងក្រោម (ឈ្មោះម្នាក់រៀងរាល់មួយបន្ទាត់)៖",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = bulkStudentsText,
                        onValueChange = { bulkStudentsText = it },
                        placeholder = { Text("ម៉ៅ សុខា\nចាន់ បូរី\nកែវ ដារ៉ា\nសំ ផាឡា") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val names = bulkStudentsText.split("\n")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        if (names.isNotEmpty()) {
                            var currentRoll = studentsInClass.size + 1
                            names.forEach { name ->
                                viewModel.addStudent(
                                    classId = activeClassroom!!.id,
                                    name = name,
                                    gender = bulkStudentsGender,
                                    rollNumber = currentRoll
                                )
                                currentRoll++
                            }
                            Toast.makeText(context, "បានបញ្ចូលសិស្សចំនួន ${names.size} នាក់ជោគជ័យ", Toast.LENGTH_SHORT).show()
                            bulkStudentsText = ""
                            showAddBulkStudentDialog = false
                        } else {
                            Toast.makeText(context, "សូមបញ្ចូលឈ្មោះសិស្សយ៉ាងហោចណាស់ម្នាក់", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("រក្សាទុកទាំងអស់")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBulkStudentDialog = false }) {
                    Text("បោះបង់")
                }
            }
        )
    }
}
