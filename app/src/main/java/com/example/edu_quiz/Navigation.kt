package com.example.edu_quiz

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.edu_quiz.data.DefaultDataRepository
import com.example.edu_quiz.ui.leaderboard.LeaderboardScreen
import com.example.edu_quiz.ui.main.MainScreen
import com.example.edu_quiz.ui.mistakes.WrongAnswersScreen
import com.example.edu_quiz.ui.quiz.QuizScreen
import com.example.edu_quiz.ui.study.StudyContentScreen
import com.example.edu_quiz.StudyContent
import com.example.edu_quiz.ui.quiz.QuizResultScreen
import com.example.edu_quiz.ui.settings.SettingsScreen

@Composable
fun MainNavigation() {
  val context = LocalContext.current.applicationContext
  val repository = remember { DefaultDataRepository(context) }
  val backStack = rememberNavBackStack(Main)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    transitionSpec = {
      slideInHorizontally(
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        initialOffsetX = { it }
      ) + fadeIn(
        animationSpec = tween(250, easing = FastOutSlowInEasing)
      ) togetherWith slideOutHorizontally(
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        targetOffsetX = { -it / 3 }
      ) + fadeOut(
        animationSpec = tween(250, easing = FastOutSlowInEasing)
      )
    },
    popTransitionSpec = {
      slideInHorizontally(
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        initialOffsetX = { -it / 3 }
      ) + fadeIn(
        animationSpec = tween(250, easing = FastOutSlowInEasing)
      ) togetherWith slideOutHorizontally(
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        targetOffsetX = { it }
      ) + fadeOut(
        animationSpec = tween(250, easing = FastOutSlowInEasing)
      )
    },
    entryProvider = entryProvider {
      // Main screen / Category Tree Picker
      entry<Main> {
        MainScreen(
          onItemClick = { navKey -> backStack.add(navKey) },
          repository = repository,
          modifier = Modifier.safeDrawingPadding()
        )
      }

      // Quiz play screen
      entry<QuizPlay> { key ->
        QuizScreen(
          categoryIds = key.categoryIds,
          isPracticeMistakes = key.isPracticeMistakes,
          sessionId = key.sessionId,
          onCloseClick = { backStack.removeLastOrNull() },
          onQuizComplete = { score, total ->
            // Replace play screen on the stack with the result screen
            backStack.removeLastOrNull()
            backStack.add(QuizResult(score, total, key.categoryIds.joinToString(", ")))
          },
          repository = repository,
          modifier = Modifier.safeDrawingPadding()
        )
      }

      // Quiz result summary screen
      entry<QuizResult> { key ->
        QuizResultScreen(
          score = key.score,
          totalQuestions = key.totalQuestions,
          categoryNames = key.categoryNames,
          onBackClick = {
            backStack.removeLastOrNull()
          },
          onPracticeMistakesClick = {
            backStack.removeLastOrNull()
            backStack.add(PracticeMistakes)
          },
          modifier = Modifier.safeDrawingPadding()
        )
      }

      // Practice mistakes list screen
      entry<PracticeMistakes> {
        WrongAnswersScreen(
          onBackClick = { backStack.removeLastOrNull() },
          onStartPracticeClick = {
            backStack.add(QuizPlay(categoryIds = emptyList(), isPracticeMistakes = true, sessionId = System.currentTimeMillis()))
          },
          repository = repository,
          modifier = Modifier.safeDrawingPadding()
        )
      }

      // Quiz attempts history log screen
      entry<Leaderboard> {
        LeaderboardScreen(
          onBackClick = { backStack.removeLastOrNull() },
          repository = repository,
          modifier = Modifier.safeDrawingPadding()
        )
      }

      // Study content screen
      entry<StudyContent> { key ->
        StudyContentScreen(
            categoryId = key.categoryId,
            categoryName = key.categoryName,
            repository = repository,
            onBack = { backStack.removeLastOrNull() }
        )
      }

      // Settings screen
      entry<Settings> {
        SettingsScreen(
          onBackClick = { backStack.removeLastOrNull() },
          repository = repository,
          modifier = Modifier.safeDrawingPadding()
        )
      }
    }
  )
}
