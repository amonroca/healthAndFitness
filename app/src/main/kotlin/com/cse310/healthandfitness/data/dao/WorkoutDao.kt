package com.cse310.healthandfitness.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cse310.healthandfitness.data.entities.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
// DAO interface for accessing workout data in the database. Provides methods for inserting, updating, deleting, and querying workouts.
interface WorkoutDao {
    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Int): WorkoutEntity?

    @Query("SELECT * FROM workouts ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE activityType = :activityType ORDER BY timestamp DESC")
    fun getWorkoutsByType(activityType: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getWorkoutsByDateRange(startTime: Long, endTime: Long): Flow<List<WorkoutEntity>>

    @Query("SELECT SUM(caloriesBurned) FROM workouts")
    fun getTotalCaloriesBurned(): Flow<Double?>

    @Query("SELECT SUM(duration) FROM workouts")
    fun getTotalDuration(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM workouts")
    fun getTotalWorkoutCount(): Flow<Int>

    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts()
}
