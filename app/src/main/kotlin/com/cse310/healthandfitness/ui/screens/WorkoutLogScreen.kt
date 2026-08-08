package com.cse310.healthandfitness.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.cse310.healthandfitness.util.LocationManager
import com.cse310.healthandfitness.ui.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch

@Composable
fun WorkoutLogScreen(viewModel: WorkoutViewModel, onNavigateToHistory: () -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationManager = remember(context) { LocationManager(context) }

    var selectedActivity by remember { mutableStateOf("Running") }
    var duration by remember { mutableStateOf("") }
    var selectedIntensity by remember { mutableStateOf("Moderate") }
    var notes by remember { mutableStateOf("") }
    var showActivityMenu by remember { mutableStateOf(false) }
    var showIntensityMenu by remember { mutableStateOf(false) }
    var showSummary by remember { mutableStateOf(false) }
    var estimatedCalories by remember { mutableStateOf(0.0) }
    var saveMessage by remember { mutableStateOf("") }
    var isSavingWorkout by remember { mutableStateOf(false) }

    val activityTypes = listOf("Running", "Walking", "Cycling", "Swimming", "Yoga", "Strength Training")
    val intensityLevels = listOf("Low", "Moderate", "High")

    fun resetForm() {
        selectedActivity = "Running"
        duration = ""
        selectedIntensity = "Moderate"
        notes = ""
        showSummary = false
        isSavingWorkout = false
    }

    fun saveWorkout(latitude: Double = 0.0, longitude: Double = 0.0, message: String) {
        viewModel.addWorkout(
            activityType = selectedActivity,
            duration = duration.toIntOrNull() ?: 0,
            intensity = selectedIntensity,
            latitude = latitude,
            longitude = longitude,
            notes = notes
        )
        saveMessage = message
        resetForm()
    }

    fun saveWorkoutWithLocation() {
        isSavingWorkout = true
        coroutineScope.launch {
            val location = locationManager.getCurrentLocation()
            if (location != null) {
                saveWorkout(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    message = "Workout saved with your current location."
                )
            } else {
                saveWorkout(message = "Workout saved, but your current location was unavailable.")
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasPermission) {
            saveWorkoutWithLocation()
        } else {
            saveWorkout(message = "Workout saved without location because permission was denied.")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Log Your Workout",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Activity Type", style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = { showActivityMenu = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedActivity)
                }
                DropdownMenu(
                    expanded = showActivityMenu,
                    onDismissRequest = { showActivityMenu = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    activityTypes.forEach { activity ->
                        DropdownMenuItem(
                            text = { Text(activity) },
                            onClick = {
                                selectedActivity = activity
                                showActivityMenu = false
                            }
                        )
                    }
                }

                Text("Duration (minutes)", style = MaterialTheme.typography.titleMedium)
                TextField(
                    value = duration,
                    onValueChange = { duration = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter duration") },
                    singleLine = true
                )

                Text("Intensity Level", style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = { showIntensityMenu = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedIntensity)
                }
                DropdownMenu(
                    expanded = showIntensityMenu,
                    onDismissRequest = { showIntensityMenu = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    intensityLevels.forEach { intensity ->
                        DropdownMenuItem(
                            text = { Text(intensity) },
                            onClick = {
                                selectedIntensity = intensity
                                showIntensityMenu = false
                            }
                        )
                    }
                }

                Text("Notes (optional)", style = MaterialTheme.typography.titleMedium)
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    label = { Text("Add any notes") },
                    maxLines = 3
                )
            }
        }

        Button(
            onClick = {
                if (duration.isNotEmpty()) {
                    estimatedCalories = calculateCalories(selectedActivity, duration.toIntOrNull() ?: 0, selectedIntensity)
                    showSummary = true
                    saveMessage = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Calculate & Submit")
        }

        if (showSummary && duration.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Workout Summary", style = MaterialTheme.typography.titleLarge)
                    Text("Activity: $selectedActivity")
                    Text("Duration: $duration minutes")
                    Text("Intensity: $selectedIntensity")
                    Text(
                        text = "Estimated Calories Burned: %.1f kcal".format(estimatedCalories),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (notes.isNotEmpty()) {
                        Text("Notes: $notes")
                    }

                    Button(
                        onClick = {
                            if (hasLocationPermission(context)) {
                                saveWorkoutWithLocation()
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSavingWorkout
                    ) {
                        Text(if (isSavingWorkout) "Saving..." else "Save Workout")
                    }

                    OutlinedButton(
                        onClick = { showSummary = false },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSavingWorkout
                    ) {
                        Text("Edit")
                    }
                }
            }
        }

        if (saveMessage.isNotEmpty()) {
            Text(
                text = saveMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        OutlinedButton(
            onClick = onNavigateToHistory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("View Workout History")
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}

private fun calculateCalories(activity: String, duration: Int, intensity: String): Double {
    val baseCalories = when (activity.lowercase()) {
        "running" -> 10.0
        "walking" -> 4.0
        "cycling" -> 8.0
        "swimming" -> 11.0
        "yoga" -> 3.5
        "strength training" -> 6.0
        else -> 5.0
    }

    val intensityMultiplier = when (intensity.lowercase()) {
        "low" -> 1.0
        "moderate" -> 1.5
        "high" -> 2.0
        else -> 1.5
    }

    return baseCalories * duration * intensityMultiplier
}
