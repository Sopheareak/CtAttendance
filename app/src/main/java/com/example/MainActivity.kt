package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ManageScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AttendanceViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppFrame()
      }
    }
  }
}

@Composable
fun MainAppFrame(
  viewModel: AttendanceViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsState()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.navigationBars)
          .testTag("main_bottom_nav")
      ) {
        NavigationBarItem(
          selected = currentScreen == "dashboard",
          onClick = { viewModel.navigateTo("dashboard") },
          icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard Tab") },
          label = { Text("ផ្ទៃតាប្លូ", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
          modifier = Modifier.testTag("nav_dashboard_tab")
        )

        NavigationBarItem(
          selected = currentScreen == "record",
          onClick = { viewModel.navigateTo("record") },
          icon = { Icon(Icons.Default.HowToReg, contentDescription = "Record Tab") },
          label = { Text("ស្រង់វត្តមាន", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
          modifier = Modifier.testTag("nav_record_tab")
        )

        NavigationBarItem(
          selected = currentScreen == "reports",
          onClick = { viewModel.navigateTo("reports") },
          icon = { Icon(Icons.Default.Assessment, contentDescription = "Reports Tab") },
          label = { Text("របាយការណ៍", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
          modifier = Modifier.testTag("nav_reports_tab")
        )

        NavigationBarItem(
          selected = currentScreen == "manage",
          onClick = { viewModel.navigateTo("manage") },
          icon = { Icon(Icons.Default.Settings, contentDescription = "Manage Tab") },
          label = { Text("គ្រប់គ្រង", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
          modifier = Modifier.testTag("nav_manage_tab")
        )
      }
    }
  ) { innerPadding ->
    val modifier = Modifier.padding(innerPadding)
    when (currentScreen) {
      "dashboard" -> DashboardScreen(viewModel = viewModel, modifier = modifier)
      "record" -> AttendanceScreen(viewModel = viewModel, modifier = modifier)
      "reports" -> ReportScreen(viewModel = viewModel, modifier = modifier)
      "manage" -> ManageScreen(viewModel = viewModel, modifier = modifier)
      else -> DashboardScreen(viewModel = viewModel, modifier = modifier)
    }
  }
}

