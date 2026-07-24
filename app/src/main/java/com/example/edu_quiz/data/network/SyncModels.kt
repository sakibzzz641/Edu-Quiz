package com.example.edu_quiz.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryEntry(
  val name: String,
  val file: String,
  val version: Int
)

@Serializable
data class IndexJson(
  val categories: List<CategoryEntry>
)

@Serializable
data class QuestionJson(
  val id: String,
  @SerialName("category_path") val categoryPath: String,
  val question: String,
  val options: List<String>,
  @SerialName("correct_answer_index") val correctAnswerIndex: Int,
  val explanation: String
)

@Serializable
data class CategoryQuestionsJson(
  val category: String,
  val version: Int,
  val questions: List<QuestionJson>
)

@Serializable
data class StudyTopicItemJson(
  @SerialName("category_path") val categoryPath: String,
  val file: String,
  val version: Int
)

@Serializable
data class StudyIndexJson(
  val topics: List<StudyTopicItemJson> = emptyList()
)
