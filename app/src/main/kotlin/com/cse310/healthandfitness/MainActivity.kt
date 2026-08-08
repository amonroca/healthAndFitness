package com.cse310.healthandfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.cse310.healthandfitness.data.database.HealthAndFitnessDatabase
import com.cse310.healthandfitness.data.repository.WorkoutRepository
import com.cse310.healthandfitness.ui.screens.WorkoutHistoryScreen
import com.cse310.healthandfitness.ui.screens.WorkoutLogScreen
import com.cse310.healthandfitness.ui.theme.HealthAndFitnessTheme
import com.cse310.healthandfitness.ui.viewmodel.WorkoutViewModel
import com.cse310.healthandfitness.ui.viewmodel.WorkoutViewModelFactory

// Hosts the app UI and switches between the workout log and history screens.
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: WorkoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Database and Repository
        val database = HealthAndFitnessDatabase.getDatabase(this)
        val repository = WorkoutRepository(database.workoutDao())
        val factory = WorkoutViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]

        setContent {
            HealthAndFitnessTheme {
                var selectedTab by remember { mutableIntStateOf(0) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Log Workout") },
                                label = { Text("Log") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = { Icon(Icons.Default.List, contentDescription = "Workout History") },
                                label = { Text("History") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (selectedTab) {
                            0 -> WorkoutLogScreen(
                                viewModel = viewModel,
                                onNavigateToHistory = { selectedTab = 1 }
                            )
                            1 -> WorkoutHistoryScreen(
                                viewModel = viewModel,
                                onNavigateToLog = { selectedTab = 0 }
                            )
                        }
                    }
                }
            }
        }
    }
}
