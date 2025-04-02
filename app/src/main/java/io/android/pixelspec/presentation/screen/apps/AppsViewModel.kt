package io.android.pixelspec.presentation.screen.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.android.pixelspec.domain.model.AppInfo
import io.android.pixelspec.domain.usecase.GetInstalledAppsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val getInstalledApps: GetInstalledAppsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AppsState())
    val state: StateFlow<AppsState> = _state.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val apps = getInstalledApps()
                _state.value = _state.value.copy(
                    apps = apps, isLoading = false, error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to load apps", isLoading = false
                )
            }
        }
    }
}

data class AppsState(
    val apps: List<AppInfo> = emptyList(), val isLoading: Boolean = false, val error: String? = null
)