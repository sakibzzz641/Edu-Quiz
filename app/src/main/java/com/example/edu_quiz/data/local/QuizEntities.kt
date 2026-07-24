package com.example.edu_quiz.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val parentId: Long?,        // null means top-level category
  val fullPath: String        // "English > Grammar > Parts of Speech > Noun"
)

@Entity(
  tableName = "questions",
  foreignKeys = [
    ForeignKey(
      entity = CategoryEntity::class,
      parentColumns = ["id"],
      childColumns = ["categoryId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("categoryId")]
)
data class QuestionEntity(
  @PrimaryKey val id: String,          // e.g. "eng_0001"
  val categoryId: Long,
  val questionText: String,
  val option1: String,
  val option2: String,
  val option3: String,
  val option4: String,
  val correctIndex: Int,               // 0-indexed correct answer
  val explanation: String
)

@Entity(tableName = "wrong_answers")
data class WrongAnswerEntity(
  @PrimaryKey val questionId: String,
  val wrongCount: Int,
  val lastAttempted: Long
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val date: Long,
  val categoryIds: String,            // Comma-separated list of category ids
  val score: Int,
  val totalQuestions: Int
)

@Entity(tableName = "sync_meta")
data class SyncMetaEntity(
  @PrimaryKey val categoryName: String,
  val lastSyncedVersion: Int
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
  @PrimaryKey val key: String,         // e.g. "json_base_url"
  val value: String
)

@Entity(tableName = "study_content")
data class StudyContentEntity(
  @PrimaryKey val categoryId: Long,
  val markdownContent: String,
  val version: Int
)
