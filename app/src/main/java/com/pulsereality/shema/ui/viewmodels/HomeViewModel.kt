package com.pulsereality.shema.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsereality.shema.data.model.Devotion
import com.pulsereality.shema.data.model.DevotionLocale
import com.pulsereality.shema.data.repository.DevotionRepository
import com.pulsereality.shema.data.repository.FakeDevotionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DevotionUiState {
    data object Loading : DevotionUiState
    data class Success(
        val core: Devotion,
        val locale: DevotionLocale,
        val completedSteps: Set<Int> = setOf(1)
    ) : DevotionUiState
    data class Error(val message: String) : DevotionUiState
}

class HomeViewModel(
    private val devotionRepository: DevotionRepository = FakeDevotionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DevotionUiState>(DevotionUiState.Loading)
    val uiState: StateFlow<DevotionUiState> = _uiState.asStateFlow()

    private val _completedSteps = MutableStateFlow<Set<Int>>(setOf(1))
    val completedSteps: StateFlow<Set<Int>> = _completedSteps.asStateFlow()

    private val _streakDays = MutableStateFlow(7)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    init {
        loadTodayDevotion()
    }

    fun loadTodayDevotion(lang: String = "en") {
        viewModelScope.launch {
            _uiState.value = DevotionUiState.Loading
            devotionRepository.getDevotion(lang = lang)
                .onSuccess { (core, locale) ->
                    _uiState.value = DevotionUiState.Success(
                        core = core,
                        locale = locale,
                        completedSteps = _completedSteps.value
                    )
                }
                .onFailure { error ->
                    _uiState.value = DevotionUiState.Error(error.localizedMessage ?: "Failed to load devotion")
                }
        }
    }

    fun markStepCompleted(step: Int) {
        _completedSteps.value = _completedSteps.value + step
        val current = _uiState.value
        if (current is DevotionUiState.Success) {
            _uiState.value = current.copy(completedSteps = _completedSteps.value)
        }
    }

    fun setStreakDays(days: Int) {
        _streakDays.value = days
    }
}
