package com.pulsereality.shema.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsereality.shema.data.model.PrayerRequest
import com.pulsereality.shema.data.repository.FakePrayerRepository
import com.pulsereality.shema.data.repository.PrayerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val prayerRepository: PrayerRepository = FakePrayerRepository()
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    val activePrayer: StateFlow<PrayerRequest?> = prayerRepository.getActivePrayer()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val communityPrayers: StateFlow<List<PrayerRequest>> = combine(
        prayerRepository.getCommunityPrayers(),
        _selectedFilter
    ) { list, filter ->
        if (filter == "All") {
            list
        } else {
            list.filter { it.type.equals(filter, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tracks which prayer requests the local user has prayed for in this session
    private val _prayedRequests = MutableStateFlow<Set<String>>(emptySet())
    val prayedRequests: StateFlow<Set<String>> = _prayedRequests.asStateFlow()

    // 30-minute rotating countdown timer (in seconds)
    private val _countdownSeconds = MutableStateFlow(1080) // 18 minutes remaining
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    init {
        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _countdownSeconds.value
                if (current > 0) {
                    _countdownSeconds.value = current - 1
                } else {
                    _countdownSeconds.value = 1800 // Reset 30-min window
                }
            }
        }
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun prayForRequest(prayerId: String) {
        if (_prayedRequests.value.contains(prayerId)) return
        _prayedRequests.value = _prayedRequests.value + prayerId

        viewModelScope.launch {
            prayerRepository.incrementPrayCount(prayerId)
        }
    }

    fun submitPrayer(title: String, body: String, author: String, type: String = "general") {
        viewModelScope.launch {
            prayerRepository.submitPrayer(title, body, author)
        }
    }
}
