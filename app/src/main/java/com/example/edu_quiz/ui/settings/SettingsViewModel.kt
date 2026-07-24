package com.example.edu_quiz.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edu_quiz.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: DataRepository) : ViewModel() {

  private val _baseUrl = MutableStateFlow("")
  val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

  init {
    loadBaseUrl()
  }

  fun loadBaseUrl() {
    viewModelScope.launch {
      _baseUrl.value = repository.getBaseUrl()
    }
  }

  fun saveBaseUrl(url: String) {
    viewModelScope.launch {
      repository.saveBaseUrl(url)
      _baseUrl.value = url
    }
  }
}
