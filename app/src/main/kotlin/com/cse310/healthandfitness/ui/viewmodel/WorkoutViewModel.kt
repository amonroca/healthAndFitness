package com.cse310.healthandfitness.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cse310.healthandfitness.data.entities.WorkoutEntity
import com.cse310.healthandfitness.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Holds workout UI state and coordinates actions between the UI and repository.
class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _workoutState = MutableStateFlow<List<WorkoutEntity>>(emptyList())
    val workoutState: StateFlow<List<WorkoutEntity>> = _workoutState.asStateFlow()

    private val _selectedWorkout = MutableStateFlow<WorkoutEntity?>(null)
    val selectedWorkout: StateFlow<WorkoutEntity?> = _selectedWorkout.asStateFlow()

    private val _totalCalories = MutableStateFlow(0.0)
    val totalCalories: StateFlow<Double> = _totalCalories.asStateFlow()

    private val _totalDuration = MutableStateFlow(0)
    val totalDuration: StateFlow<Int> = _totalDuration.asStateFlow()

    private val _totalWorkouts = MutableStateFlow(0)
    val totalWorkouts: StateFlow<Int> = _totalWorkouts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAllWorkouts()
        loadStats()
    }

    private fun loadAllWorkouts() {
        viewModelScope.launch {
            repository.getAllWorkouts().collect { workouts ->
                _workoutState.value = workouts
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            repository.getTotalCaloriesBurned().collect { calories ->
                _totalCalories.value = calories ?: 0.0
            }
        }
        viewModelScope.launch {
            repository.getTotalDuration().collect { duration ->
                _totalDuration.value = duration ?: 0
            }
        }
        viewModelScope.launch {
            repository.getTotalWorkoutCount().collect { count ->
                _totalWorkouts.value = count
            }
        }
    }

    fun addWorkout(
        activityType: String,
        duration: Int,
        intensity: String,
        distance: Double = 0.0,
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        routeCoordinates: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val calories = repository.calculateCaloriesBurned(activityType, duration, intensity)
            val workout = WorkoutEntity(
                activityType = activityType,
                duration = duration,
                intensity = intensity,
                caloriesBurned = calories,
                distance = distance,
                latitude = latitude,
                longitude = longitude,
                routeCoordinates = routeCoordinates,
                notes = notes
            )
            repository.insertWorkout(workout)
            _isLoading.value = false
        }
    }

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    fun selectWorkout(workout: WorkoutEntity) {
        _selectedWorkout.value = workout
    }

    fun clearSelection() {
        _selectedWorkout.value = null
    }

    fun getWorkoutsByType(activityType: String) {
        viewModelScope.launch {
            repository.getWorkoutsByType(activityType).collect { workouts ->
                _workoutState.value = workouts
            }
        }
    }
}

// Creates WorkoutViewModel instances with the repository dependency.
class WorkoutViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            return WorkoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
