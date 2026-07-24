package com.example.edu_quiz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
  @Query("""
    WITH RECURSIVE category_tree(id) AS (
        SELECT id FROM categories WHERE id IN (:selectedIds)
        UNION ALL
        SELECT c.id FROM categories c
        INNER JOIN category_tree ct ON c.parentId = ct.id
    )
    SELECT * FROM questions WHERE categoryId IN (SELECT id FROM category_tree)
  """)
  suspend fun getQuestionsForCategories(selectedIds: List<Long>): List<QuestionEntity>

  @Query("SELECT * FROM categories")
  fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM categories")
  suspend fun getAllCategories(): List<CategoryEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<CategoryEntity>): List<Long>

  @Query("SELECT id FROM categories WHERE fullPath = :fullPath LIMIT 1")
  suspend fun getCategoryIdByFullPath(fullPath: String): Long?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertQuestions(questions: List<QuestionEntity>): List<Long>

  @Query("DELETE FROM questions WHERE categoryId = :categoryId")
  suspend fun deleteQuestionsForCategory(categoryId: Long): Int

  @Query("SELECT lastSyncedVersion FROM sync_meta WHERE categoryName = :categoryName LIMIT 1")
  suspend fun getSyncedVersion(categoryName: String): Int?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSyncMeta(meta: SyncMetaEntity): Long

  @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
  suspend fun getSetting(key: String): String?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSetting(setting: AppSettingsEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWrongAnswer(wrongAnswer: WrongAnswerEntity): Long

  @Query("SELECT * FROM wrong_answers ORDER BY lastAttempted DESC")
  fun getWrongAnswersFlow(): Flow<List<WrongAnswerEntity>>

  @Query("SELECT * FROM wrong_answers ORDER BY lastAttempted DESC")
  suspend fun getWrongAnswers(): List<WrongAnswerEntity>

  @Query("SELECT * FROM questions WHERE id IN (:ids)")
  suspend fun getQuestionsByIds(ids: List<String>): List<QuestionEntity>

  @Query("DELETE FROM wrong_answers WHERE questionId = :questionId")
  suspend fun removeWrongAnswer(questionId: String): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAttempt(attempt: QuizAttemptEntity): Long

  @Query("SELECT * FROM quiz_attempts ORDER BY date DESC")
  fun getAttemptsFlow(): Flow<List<QuizAttemptEntity>>

  @Query("SELECT * FROM study_content WHERE categoryId = :categoryId LIMIT 1")
  suspend fun getStudyContent(categoryId: Long): StudyContentEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStudyContent(content: StudyContentEntity): Long
}
