package com.example.edu_quiz.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    CategoryEntity::class,
    QuestionEntity::class,
    WrongAnswerEntity::class,
    QuizAttemptEntity::class,
    SyncMetaEntity::class,
    AppSettingsEntity::class,
    StudyContentEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {
  abstract fun quizDao(): QuizDao

  companion object {
    @Volatile
    private var INSTANCE: QuizDatabase? = null

    fun getDatabase(context: Context): QuizDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          QuizDatabase::class.java,
          "quiz_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
