package com.example.edu_quiz.ui.main

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edu_quiz.data.DataRepository
import com.example.edu_quiz.data.local.CategoryEntity
import com.example.edu_quiz.data.local.QuizAttemptEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(private val repository: DataRepository) : ViewModel() {

  val categories: StateFlow<List<CategoryEntity>> = repository.categories
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val attempts: StateFlow<List<QuizAttemptEntity>> = repository.attempts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val wrongAnswersCount: StateFlow<Int> = repository.wrongAnswersCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  private val _syncState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
  val syncState: StateFlow<SyncUiState> = _syncState.asStateFlow()

  val selectedCategoryIds = mutableStateListOf<Long>()

  fun toggleCategorySelection(categoryId: Long) {
    if (selectedCategoryIds.contains(categoryId)) {
      selectedCategoryIds.remove(categoryId)
    } else {
      selectedCategoryIds.add(categoryId)
    }
  }

  fun clearSelection() {
    selectedCategoryIds.clear()
  }

  fun syncData() {
    viewModelScope.launch {
      _syncState.value = SyncUiState.Loading("Checking index.json...")
      repository.syncData { progress ->
        _syncState.value = SyncUiState.Loading(progress)
      }.onSuccess {
        _syncState.value = SyncUiState.Success
        // Debug log of all categories after sync
        viewModelScope.launch {
          try {
            val cats = repository.getAllCategories()
            Log.d("SyncDebug", "Categories after sync: $cats")
          } catch (e: Exception) {
            Log.e("SyncDebug", "Failed to fetch categories: ${e.message}")
          }
        }
      }.onFailure { throwable ->
        _syncState.value = SyncUiState.Error(throwable.message ?: "Unknown sync error")
      }
    }
  }

  fun resetSyncState() {
    _syncState.value = SyncUiState.Idle
  }
}

sealed interface SyncUiState {
  object Idle : SyncUiState
  data class Loading(val message: String) : SyncUiState
  object Success : SyncUiState
  data class Error(val message: String) : SyncUiState
}
