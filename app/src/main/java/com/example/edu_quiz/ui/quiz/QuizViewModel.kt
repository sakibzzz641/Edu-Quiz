package com.example.edu_quiz.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edu_quiz.data.DataRepository
import com.example.edu_quiz.data.local.QuestionEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ShuffledQuestion(
  val question: QuestionEntity,
  val shuffledOptions: List<String>,
  val shuffledCorrectIndex: Int
)

sealed interface QuizUiState {
  object Loading : QuizUiState
  data class Success(val questions: List<ShuffledQuestion>) : QuizUiState
  object Empty : QuizUiState
}

class QuizViewModel(
  private val repository: DataRepository,
  private val categoryIds: List<Long>,
  private val isPracticeMistakes: Boolean,
  private val sessionId: Long = 0L
) : ViewModel() {

  private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
  val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

  private val _currentQuestionIndex = MutableStateFlow(0)
  val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

  private val _score = MutableStateFlow(0)
  val score: StateFlow<Int> = _score.asStateFlow()

  private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
  val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

  // Smooth timer ratio from 1.0f down to 0.0f
  private val _timerRatio = MutableStateFlow(1.0f)
  val timerRatio: StateFlow<Float> = _timerRatio.asStateFlow()

  private var timerJob: Job? = null
  private var questionsList = emptyList<ShuffledQuestion>()
  private var totalTimerMs = 10000L // 10 seconds

  init {
    resetState()
    loadQuestions()
  }

  fun resetState() {
    stopTimer()
    _uiState.value = QuizUiState.Loading
    _currentQuestionIndex.value = 0
    _score.value = 0
    _selectedAnswerIndex.value = null
    _timerRatio.value = 1.0f
    questionsList = emptyList()
  }

  private fun loadQuestions() {
    viewModelScope.launch {
      _uiState.value = QuizUiState.Loading
      val rawQuestions = if (isPracticeMistakes) {
        repository.getQuestionsForPracticeMistakes()
      } else {
        repository.getQuestionsForCategories(categoryIds)
      }

      if (rawQuestions.isEmpty()) {
        _uiState.value = QuizUiState.Empty
      } else {
        // Shuffle questions (Fisher-Yates) and their options
        val shuffled = rawQuestions.shuffled().map { q ->
          val originalOptions = listOf(q.option1, q.option2, q.option3, q.option4)
          val shuffledOptions = originalOptions.shuffled()
          val correctText = originalOptions.getOrNull(q.correctIndex) ?: ""
          val shuffledCorrectIndex = shuffledOptions.indexOf(correctText).coerceAtLeast(0)
          ShuffledQuestion(q, shuffledOptions, shuffledCorrectIndex)
        }
        questionsList = shuffled
        _uiState.value = QuizUiState.Success(shuffled)
        startTimerForCurrentQuestion()
      }
    }
  }

  fun selectOption(index: Int) {
    if (_selectedAnswerIndex.value != null || _timerRatio.value <= 0f) return
    stopTimer()
    _selectedAnswerIndex.value = index

    val currentQ = questionsList.getOrNull(_currentQuestionIndex.value) ?: return
    if (index == currentQ.shuffledCorrectIndex) {
      _score.value = _score.value + 1
      // If practicing mistakes, we can remove it from wrong answers if they got it right!
      if (isPracticeMistakes) {
        viewModelScope.launch {
          repository.removeWrongAnswer(currentQ.question.id)
        }
      }
    } else {
      // Answered wrong: log in wrong answers DB
      viewModelScope.launch {
        repository.addWrongAnswer(currentQ.question.id)
      }
    }
  }

  private fun handleTimeout() {
    if (_selectedAnswerIndex.value != null) return
    _selectedAnswerIndex.value = -1 // Indicates timeout (incorrect)
    
    val currentQ = questionsList.getOrNull(_currentQuestionIndex.value) ?: return
    viewModelScope.launch {
      repository.addWrongAnswer(currentQ.question.id)
    }
  }

  fun nextQuestion(onQuizComplete: (Int, Int) -> Unit) {
    val nextIndex = _currentQuestionIndex.value + 1
    if (nextIndex < questionsList.size) {
      _currentQuestionIndex.value = nextIndex
      _selectedAnswerIndex.value = null
      _timerRatio.value = 1.0f
      startTimerForCurrentQuestion()
    } else {
      stopTimer()
      // Save attempt in leaderboard
      viewModelScope.launch {
        repository.saveQuizAttempt(
          categoryIds = categoryIds,
          score = _score.value,
          totalQuestions = questionsList.size
        )
        onQuizComplete(_score.value, questionsList.size)
      }
    }
  }

  private fun startTimerForCurrentQuestion() {
    stopTimer()
    _timerRatio.value = 1.0f
    
    val updateIntervalMs = 30L // update every 30ms for liquid-smooth movement
    val totalSteps = totalTimerMs / updateIntervalMs
    var elapsedSteps = 0L

    timerJob = viewModelScope.launch {
      while (elapsedSteps < totalSteps && _selectedAnswerIndex.value == null) {
        delay(updateIntervalMs)
        elapsedSteps++
        val ratio = 1.0f - (elapsedSteps.toFloat() / totalSteps.toFloat())
        _timerRatio.value = ratio.coerceIn(0f, 1f)
      }
      if (_selectedAnswerIndex.value == null) {
        handleTimeout()
      }
    }
  }

  private fun stopTimer() {
    timerJob?.cancel()
    timerJob = null
  }

  override fun onCleared() {
    super.onCleared()
    stopTimer()
  }
}
