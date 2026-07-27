package com.example.edu_quiz.data

import android.content.Context
import com.example.edu_quiz.data.local.AppSettingsEntity
import com.example.edu_quiz.data.local.CategoryEntity
import com.example.edu_quiz.data.local.StudyContentEntity
import com.example.edu_quiz.data.local.QuestionEntity
import com.example.edu_quiz.data.local.QuizAttemptEntity
import com.example.edu_quiz.data.local.QuizDatabase
import com.example.edu_quiz.data.local.SyncMetaEntity
import com.example.edu_quiz.data.local.WrongAnswerEntity
import com.example.edu_quiz.data.network.IndexJson
import com.example.edu_quiz.data.network.CategoryQuestionsJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import com.example.edu_quiz.data.network.StudyIndexJson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

interface DataRepository {
  suspend fun getAllCategories(): List<com.example.edu_quiz.data.local.CategoryEntity>
  val categories: Flow<List<CategoryEntity>>
  val wrongAnswersCount: Flow<Int>
  val attempts: Flow<List<QuizAttemptEntity>>
  
  suspend fun getQuestionsForCategories(selectedIds: List<Long>): List<QuestionEntity>
  suspend fun getQuestionsForPracticeMistakes(): List<QuestionEntity>
  suspend fun saveQuizAttempt(categoryIds: List<Long>, score: Int, totalQuestions: Int)
  suspend fun addWrongAnswer(questionId: String)
  suspend fun removeWrongAnswer(questionId: String)
  suspend fun getWrongAnswersList(): List<QuestionEntity>
  suspend fun getBaseUrl(): String
  suspend fun getStudyContent(categoryId: Long): com.example.edu_quiz.data.local.StudyContentEntity?
  suspend fun saveBaseUrl(url: String)
  suspend fun syncData(onProgress: (String) -> Unit): Result<Unit>
}

class DefaultDataRepository(context: Context) : DataRepository {
  private val db = QuizDatabase.getDatabase(context)
  private val dao = db.quizDao()
  private val client = OkHttpClient()
  
  private val jsonParser = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
  }

  companion object {
    const val DEFAULT_BASE_URL = "https://raw.githubusercontent.com/sakibzzz641/Edu-Quiz/refs/heads/main/asset/"
    const val KEY_BASE_URL = "json_base_url"
  }

  override val categories: Flow<List<CategoryEntity>> = dao.getAllCategoriesFlow()
  
  override val wrongAnswersCount: Flow<Int> = dao.getWrongAnswersFlow().map { it.size }
  
  override val attempts: Flow<List<QuizAttemptEntity>> = dao.getAttemptsFlow()

  override suspend fun getQuestionsForCategories(selectedIds: List<Long>): List<QuestionEntity> = withContext(Dispatchers.IO) {
    dao.getQuestionsForCategories(selectedIds)
  }

  override suspend fun getQuestionsForPracticeMistakes(): List<QuestionEntity> = withContext(Dispatchers.IO) {
    val wrongIds = dao.getWrongAnswers().map { it.questionId }
    if (wrongIds.isEmpty()) emptyList() else dao.getQuestionsByIds(wrongIds)
  }

  override suspend fun getWrongAnswersList(): List<QuestionEntity> = withContext(Dispatchers.IO) {
    val wrongIds = dao.getWrongAnswers().map { it.questionId }
    if (wrongIds.isEmpty()) emptyList() else dao.getQuestionsByIds(wrongIds)
  }

  override suspend fun saveQuizAttempt(categoryIds: List<Long>, score: Int, totalQuestions: Int) {
    withContext(Dispatchers.IO) {
      dao.insertAttempt(
        QuizAttemptEntity(
          date = System.currentTimeMillis(),
          categoryIds = categoryIds.joinToString(","),
          score = score,
          totalQuestions = totalQuestions
        )
      )
    }
  }

  override suspend fun addWrongAnswer(questionId: String) {
    withContext(Dispatchers.IO) {
      dao.insertWrongAnswer(
        WrongAnswerEntity(
          questionId = questionId,
          wrongCount = 1,
          lastAttempted = System.currentTimeMillis()
        )
      )
    }
  }

  override suspend fun removeWrongAnswer(questionId: String) {
    withContext(Dispatchers.IO) {
      dao.removeWrongAnswer(questionId)
    }
  }

  override suspend fun getBaseUrl(): String = withContext(Dispatchers.IO) {
    dao.getSetting(KEY_BASE_URL) ?: DEFAULT_BASE_URL
  }

  override suspend fun getStudyContent(categoryId: Long): StudyContentEntity? = withContext(Dispatchers.IO) {
    dao.getStudyContent(categoryId)
  }

  override suspend fun saveBaseUrl(url: String) {
    withContext(Dispatchers.IO) {
      dao.insertSetting(AppSettingsEntity(KEY_BASE_URL, url))
    }
  }

    override suspend fun getAllCategories(): List<CategoryEntity> = withContext(Dispatchers.IO) {
        dao.getAllCategories()
    }

  override suspend fun syncData(onProgress: (String) -> Unit): Result<Unit> = withContext(Dispatchers.IO) {
    try {
      var baseUrl = getBaseUrl()
      if (!baseUrl.endsWith("/")) {
        baseUrl += "/"
      }

      onProgress("Fetching index.json...")
      val request = Request.Builder()
        .url(baseUrl + "index.json")
        .build()

      val response = client.newCall(request).execute()
      if (!response.isSuccessful) {
        return@withContext Result.failure(IOException("Failed to download index.json. Code: ${response.code}"))
      }

      val indexBody = response.body?.string() ?: return@withContext Result.failure(IOException("Empty index.json response"))
      val indexJson = jsonParser.decodeFromString<IndexJson>(indexBody)

      for (cat in indexJson.categories) {
        val localVersion = dao.getSyncedVersion(cat.name) ?: 0
        if (localVersion < cat.version) {
          onProgress("Downloading ${cat.name} (${cat.file})...")
          val catRequest = Request.Builder()
            .url(baseUrl + cat.file)
            .build()
          
          val catResponse = client.newCall(catRequest).execute()
          if (!catResponse.isSuccessful) {
            continue
          }

          val catBody = catResponse.body?.string() ?: continue
          val categoryData = jsonParser.decodeFromString<CategoryQuestionsJson>(catBody)

          val questionEntities = mutableListOf<QuestionEntity>()
          for (q in categoryData.questions) {
            val leafId = resolveOrCreateCategoryTree(q.categoryPath)
            questionEntities.add(
              QuestionEntity(
                id = q.id,
                categoryId = leafId,
                questionText = q.question,
                option1 = q.options.getOrElse(0) { "" },
                option2 = q.options.getOrElse(1) { "" },
                option3 = q.options.getOrElse(2) { "" },
                option4 = q.options.getOrElse(3) { "" },
                correctIndex = q.correctAnswerIndex,
                explanation = q.explanation
              )
            )
          }

          if (questionEntities.isNotEmpty()) {
            dao.insertQuestions(questionEntities)
          }

          dao.insertSyncMeta(SyncMetaEntity(cat.name, cat.version))
        }
      }

        onProgress("Fetching study_index.json...")
        val studyRequest = Request.Builder()
            .url(baseUrl + "study_index.json")
            .build()
        val studyResponse = client.newCall(studyRequest).execute()
        if (studyResponse.isSuccessful) {
            val studyBody = studyResponse.body?.string() ?: ""
            if (studyBody.isNotEmpty()) {
                val studyIndex = jsonParser.decodeFromString<StudyIndexJson>(studyBody)
                for (topic in studyIndex.topics) {
                    val syncKey = "study_" + topic.categoryPath
                    val localVersion = dao.getSyncedVersion(syncKey) ?: 0
                    if (localVersion < topic.version) {
                        onProgress("Downloading study content for ${topic.categoryPath}...")
                        val contentRequest = Request.Builder()
                            .url(baseUrl + topic.file)
                            .build()
                        val contentResponse = client.newCall(contentRequest).execute()
                        if (contentResponse.isSuccessful) {
                            val mdContent = contentResponse.body?.string() ?: ""
                            val catId = resolveOrCreateCategoryTree(topic.categoryPath)
                            dao.insertStudyContent(
                                StudyContentEntity(
                                    categoryId = catId,
                                    markdownContent = mdContent,
                                    version = topic.version
                                )
                            )
                            dao.insertSyncMeta(SyncMetaEntity(syncKey, topic.version))
                        }
                    }
                }
            }
        }
        onProgress("Sync complete!")
      // Debug log of categories
      val allCats = dao.getAllCategories()
      android.util.Log.d("SyncDebug", "All categories after sync: ${allCats.size}")
      allCats.forEach { cat ->
          android.util.Log.d("SyncDebug", "Category id=${cat.id}, name=${cat.name}, parentId=${cat.parentId}, fullPath=${cat.fullPath}")
      }
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Helper to parse category path recursively and insert missing categories.
   * Path: "English > Grammar > Parts of Speech > Noun"
   */
  private suspend fun resolveOrCreateCategoryTree(path: String): Long {
    val parts = path.split(">").map { it.trim() }.filter { it.isNotEmpty() }
    if (parts.isEmpty()) {
      return 1L // Default fallback
    }

    var parentId: Long? = null
    var currentFullPath = ""

    for (i in parts.indices) {
      val partName = parts[i]
      currentFullPath = if (currentFullPath.isEmpty()) partName else "$currentFullPath > $partName"

      val existingId = dao.getCategoryIdByFullPath(currentFullPath)
      if (existingId != null) {
        parentId = existingId
      } else {
        val newCategory = CategoryEntity(
          name = partName,
          parentId = parentId,
          fullPath = currentFullPath
        )
        val newIds = dao.insertCategories(listOf(newCategory))
        if (newIds.isNotEmpty()) {
          parentId = newIds[0]
        }
      }
    }

    return parentId ?: 1L
  }
}
