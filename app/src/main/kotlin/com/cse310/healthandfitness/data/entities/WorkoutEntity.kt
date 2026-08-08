package com.cse310.healthandfitness.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
// Represents a single workout record stored in the local database.
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val activityType: String,
    val duration: Int,
    val intensity: String,
    val caloriesBurned: Double,
    val distance: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val routeCoordinates: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
