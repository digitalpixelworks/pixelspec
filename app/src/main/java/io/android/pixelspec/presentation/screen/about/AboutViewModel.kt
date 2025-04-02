package io.android.pixelspec.presentation.screen.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {
    // App version information
    val appVersion = "1.0.0"

    // Developer information
    val developers = listOf(
        Developer("Your Name", "Role"), Developer("Team Member", "Role")
    )

    // Libraries used
    val libraries = listOf(
        Library("Jetpack Compose", "Android"),
        Library("Hilt", "Google"),
        Library("Material 3", "Google")
    )
}

data class Developer(
    val name: String, val role: String
)

data class Library(
    val name: String, val author: String
)