package com.cse310.healthandfitness.data.repository

import com.cse310.healthandfitness.data.dao.WorkoutDao
import com.cse310.healthandfitness.data.entities.WorkoutEntity
import kotlinx.coroutines.flow.Flow

// Centralizes workout data access and calorie calculation logic.
class WorkoutRepository(private val workoutDao: WorkoutDao) {
    
    suspend fun insertWorkout(workout: WorkoutEntity): Long {
        return workoutDao.insertWorkout(workout)
    }

    suspend fun updateWorkout(workout: WorkoutEntity) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(workout: WorkoutEntity) {
        workoutDao.deleteWorkout(workout)
    }

    suspend fun getWorkoutById(id: Int): WorkoutEntity? {
        return workoutDao.getWorkoutById(id)
    }

    fun getAllWorkouts(): Flow<List<WorkoutEntity>> {
        return workoutDao.getAllWorkouts()
    }

    fun getWorkoutsByType(activityType: String): Flow<List<WorkoutEntity>> {
        return workoutDao.getWorkoutsByType(activityType)
    }

    fun getWorkoutsByDateRange(startTime: Long, endTime: Long): Flow<List<WorkoutEntity>> {
        return workoutDao.getWorkoutsByDateRange(startTime, endTime)
    }

    fun getTotalCaloriesBurned(): Flow<Double?> {
        return workoutDao.getTotalCaloriesBurned()
    }

    fun getTotalDuration(): Flow<Int?> {
        return workoutDao.getTotalDuration()
    }

    fun getTotalWorkoutCount(): Flow<Int> {
        return workoutDao.getTotalWorkoutCount()
    }

    suspend fun deleteAllWorkouts() {
        workoutDao.deleteAllWorkouts()
    }

    fun calculateCaloriesBurned(activityType: String, duration: Int, intensity: String): Double {
        val baseCalories = when (activityType.lowercase()) {
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
}
