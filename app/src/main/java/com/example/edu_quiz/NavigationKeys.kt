package com.example.edu_quiz

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Main : NavKey

@Serializable
data class QuizPlay(
  val categoryIds: List<Long>,
  val isPracticeMistakes: Boolean = false,
  val sessionId: Long = System.currentTimeMillis()
) : NavKey

@Serializable
data class QuizResult(
  val score: Int,
  val totalQuestions: Int,
  val categoryNames: String
) : NavKey

@Serializable
data object PracticeMistakes : NavKey

@Serializable
data object Leaderboard : NavKey

@Serializable
data object Settings : NavKey
@Serializable
data class StudyContent(val categoryId: Long, val categoryName: String) : NavKey
