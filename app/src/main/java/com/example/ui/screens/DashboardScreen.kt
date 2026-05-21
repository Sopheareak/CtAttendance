package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.data.model.AttendanceRecord
import com.example.ui.viewmodel.AttendanceViewModel

@Composable
fun DashboardScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val classrooms by viewModel.classrooms.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val allRecords by viewModel.allAttendanceRecords.collectAsState()
    val todayDate = viewModel.getCurrentDateString()
    
    // Filter records for today
    val todayRecords = allRecords.filter { it.date == todayDate }
    val todayAbsents = todayRecords.filter { it.status == "A" || it.status == "E" }

    // Counts
    val schoolGirls = allStudents.count { it.gender == "ស្រី" }
    val schoolBoys = allStudents.count { it.gender == "ប្រុស" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("dashboard_root"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App header / School badge
        item {
            SchoolBadgeSection()
        }

        // Stats section heading
        item {
            Text(
                text = "របាយការណ៍សង្ខេបរបស់សាលា",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Main Stat Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // First Row: Classes and Students
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "ថ្នាក់រៀនសរុប",
                        value = "${classrooms.size} ថ្នាក់",
                        icon = Icons.Default.Class,
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconTint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        subtitle = "ថ្នាក់រងអនុវិទ្យាល័យ"
                    )

                    StatCard(
                        title = "សិស្សសរុបទូទាំងសាលា",
                        value = "${allStudents.size} នាក់",
                        icon = Icons.Default.Group,
                        containerColor = MaterialTheme.colorScheme.surface,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f),
                        subtitle = "អនុវិទ្យាល័យឈើទាល"
                    )
                }

                // Second Row: Presents and Absents today
                val presentToday = todayRecords.count { it.status == "P" }
                val absentToday = todayRecords.count { it.status == "A" }
                val excuseToday = todayRecords.count { it.status == "E" }
                val isDark = isSystemInDarkTheme()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "វត្តមានថ្ងៃនេះ",
                        value = "$presentToday/${todayRecords.size}",
                        icon = Icons.Default.HowToReg,
                        containerColor = if (isDark) Color(0xFF1B3B2B) else Color(0xFFE8F5E9),
                        iconTint = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f),
                        subtitle = "អត្រា: ${if (todayRecords.isNotEmpty()) (presentToday * 100 / todayRecords.size) else 0}%"
                    )

                    StatCard(
                        title = "អវត្តមានថ្ងៃនេះ",
                        value = "${absentToday + excuseToday} នាក់",
                        icon = Icons.Default.PersonOff,
                        containerColor = if (isDark) Color(0xFF4A1C1C) else Color(0xFFFFEBEE),
                        iconTint = if (isDark) Color(0xFFE57373) else Color(0xFFC62828),
                        modifier = Modifier.weight(1f),
                        subtitle = "ច្បាប់: $excuseToday | អត់ច្បាប់: $absentToday"
                    )
                }
            }
        }

        // Today's Date display
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ListAlt,
                        contentDescription = "Date Today",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ព័ត៌មានថ្ងៃនេះ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = viewModel.convertToKhmerDate(todayDate),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // List Header for absentees today
        item {
            Text(
                text = "បញ្ជីសិស្សអវត្តមានថ្ងៃនេះ (${todayAbsents.size} នាក់)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (todayAbsents.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.HowToReg,
                            contentDescription = "No absents",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "សិស្សទាំងអស់មានវត្តមានថ្ងៃនេះ!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "មិនទាន់មានវត្តមានអវត្តមានណាមួយត្រូវបានកត់ត្រានៅឡើយ ឬសិស្សទាំងអស់បានមកសាលា។",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(todayAbsents) { record ->
                val student = allStudents.firstOrNull { it.id == record.studentId }
                val classroom = classrooms.firstOrNull { it.id == record.classId }
                if (student != null) {
                    AbsentStudentRow(
                        student = student,
                        className = classroom?.name ?: "ថ្នាក់មិនស្គាល់",
                        record = record
                    )
                }
            }
        }

        // Bottom spacing padding
        item {
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun SchoolBadgeSection() {
    val isDark = isSystemInDarkTheme()
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "School Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "អនុវិទ្យាល័យឈើទាល",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ប្រព័ន្ធគ្រប់គ្រង និងស្រង់វត្តមានសិស្សប្រចាំថ្ងៃ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.width(100.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "CHEUTEAL SECONDARY SCHOOL",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Card(
        modifier = modifier.height(125.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun AbsentStudentRow(
    student: Student,
    className: String,
    record: AttendanceRecord
) {
    val isDark = isSystemInDarkTheme()
    val statusBg = if (record.status == "A") {
        if (isDark) Color(0xFF4A1C1C) else Color(0xFFFEEBEE)
    } else {
        if (isDark) Color(0xFF4A341C) else Color(0xFFFFF8E1)
    }
    val statusColor = if (record.status == "A") {
        if (isDark) Color(0xFFE57373) else Color(0xFFC62828)
    } else {
        if (isDark) Color(0xFFFFB74D) else Color(0xFFF57F17)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(statusBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (record.status == "A") "អត់" else "ច្បាប់",
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "ថ្នាក់: $className | លេខរៀង: ${student.rollNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val statusText = if (record.status == "A") "អត់ច្បាប់" else "មានច្បាប់"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
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
