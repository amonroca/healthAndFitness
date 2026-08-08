# Overview

This project helped me practice building a complete Android application with a modern architecture, local persistence, and a reactive UI. My goal was to improve as a software engineer by working through how data flows from user input to storage and back into the interface in a clean and maintainable way.

This app is a simple health and fitness tracker focused on workout logging. Users can create a workout entry by choosing an activity type, entering a duration, selecting an intensity level, and adding optional notes. After submitting a workout, the app stores it locally and shows it in the history screen, where the user can review saved workouts, see summary statistics, and delete entries.

I created this app to strengthen my understanding of Android development with Kotlin, Jetpack Compose, Room, and the MVVM pattern. It was also a practical way to learn how to connect UI state, business logic, and local data storage inside a mobile app.

[Software Demo Video](http://youtube.link.goes.here)

# Development Environment

I developed the app with Android Studio, Gradle, the Android SDK, and the Gradle wrapper included in the repository. I also used Git for version control.

The app is written in Kotlin. The main libraries and frameworks used are Jetpack Compose for the UI, Room for local database access, Kotlin Coroutines and Flow for asynchronous and reactive data handling, Material 3 components for the interface, and Google Play Services location libraries for location-related functionality.

# Useful Websites

* [Android Developers - Build your first app](https://developer.android.com/training/basics/firstapp)
* [Android Developers - Jetpack Compose](https://developer.android.com/jetpack/compose)
* [Android Developers - Room Persistence Library](https://developer.android.com/training/data-storage/room)
* [Kotlin Documentation](https://kotlinlang.org/docs/home.html)

# Future Work

* Add input validation and clearer error feedback for workout form fields.
* Expand location features to record and display workout routes on a map.
* Add real automated tests for the repository, ViewModel, and UI flows.
