package com.cse310.healthandfitness.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cse310.healthandfitness.data.dao.WorkoutDao
import com.cse310.healthandfitness.data.entities.WorkoutEntity

@Database(entities = [WorkoutEntity::class], version = 1, exportSchema = false)
// Defines the Room database and exposes the workout DAO.
abstract class HealthAndFitnessDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var instance: HealthAndFitnessDatabase? = null

        fun getDatabase(context: Context): HealthAndFitnessDatabase {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    HealthAndFitnessDatabase::class.java,
                    "health_fitness_db"
                ).build()
                instance = db
                db
            }
        }
    }
}
